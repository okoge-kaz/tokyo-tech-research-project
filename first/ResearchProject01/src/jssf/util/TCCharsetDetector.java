package jssf.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Detects a charset for a text file or stream.
 * The recognizable charsets are SJIS, EUC, UTF8, JIS, and ASCII.
 * <p>
 * �e�L�X�g�t�@�C����Ǎ��ލہC���[�U�������R�[�h���w�肵�Ȃ���΁C
 * Java��OS�̃f�t�H���g�̕����R�[�h�Ńt�@�C�����������CJava�̓����R�[�h�ł���UTF-8�ɕϊ����܂��D
 * �����o���Ƃ��ɂ�UTF-8����OS�̃f�t�H���g�̕����R�[�h�ɕϊ����ď����o���܂��D
 * ���̓���́C�قȂ�OS��ō��ꂽ�e�L�X�g�t�@�C���������ꍇ�ɖ��ƂȂ�܂��D
 * ����������h���ɂ́C�Ǎ��ݑO�Ƀt�@�C���̕����R�[�h�𔻕ʂ��C
 * ���ʂ��ꂽ�����R�[�h��InputStreamReader�Ɏw�肷�鏈�����K�v�ł��D
 * <p>
 * The detection of this algorithm is carried out by utilizing
 * the gap of the occurence ranges of the charsets in ASCII code.
 * 
 * @see java.io.InputStreamReader
 * @see java.nio.charset.Charset
 * @since 2
 * @author hmkz
 */
public class TCCharsetDetector implements Serializable {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	/*
	 * I know that my algorithm cannot distinguish SJIS, MS932, and MS943 properly.
	 * However, since the operating systems used in Ono Lab. are only Windows, Fedora�Cand CentOS,
	 * the capability is enough to detect any kind of files they created.
	 */
	public static final Charset SJIS = Charset.isSupported("MS932") ? Charset.forName("MS932") : Charset.forName("Shift_JIS");

	public static final Charset EUC = Charset.forName("EUC-JP");

	public static final Charset UTF8 = Charset.forName("UTF8");

	public static final Charset JIS = Charset.isSupported("MS932") ? Charset.forName("MS932") : Charset.forName("JIS");

	public static final Charset ASCII = Charset.forName("ASCII");

	public static final Charset UNKNOWN = null;

	/** �t�@�C�����牽�o�C�g�ǂݍ���Ŕ��ʂɗ��p���邩 */
	private int fBytesForCheck;
	
	/**
	 * �t�@�C���̐擪bytesForCheck�o�C�g�𗘗p���ĕ����R�[�h�𔻕ʂ��锻�ʊ��Ԃ��D
	 * @param bytesForCheck ���ʂɗ��p����o�C�g��
	 * @since 2 hmkz
	 */
	public TCCharsetDetector(int bytesForCheck) {
		fBytesForCheck = bytesForCheck;
	}

	/**
	 * �t�@�C���̐擪6144�o�C�g�𗘗p���ĕ����R�[�h�𔻕ʂ��锻�ʊ��Ԃ��D
	 * @since 2 hmkz
	 */
	public TCCharsetDetector() {
		this(6144);	// 2�o�C�g������3�o�C�g�����̉\��������̂�6�̔{���D
	}
	
	/**
	 * �t�@�C���̕����R�[�h�𔻒肵�C���肳�ꂽ�R�[�h�̕����Z�b�g��Ԃ��D
	 * ���ʂł��镶���R�[�h��SJIS, EUC, UTF8, JIS, ASCII�ł���C
	 * ����ȊO�̕����R�[�h���Ɣ��肳�ꂽ�ꍇ�ɂ�null��Ԃ��D
	 * @param is �����R�[�h�̔�����󂯂���̓X�g���[���D
	 * @return �t�@�C���̃G���R�[�f�B���O�Ɏg���Ă��镶���Z�b�g�D
	 * @throws FileNotFoundException �t�@�C��filename�����݂��Ȃ��Ƃ��D
	 * @throws IOException �t�@�C���̓ǂݍ��ݒ��ɃG���[�����������Ƃ��D
	 * @since 2 hmkz
	 */
	public Charset detectCharset(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		// �ŏ��̃A�X�L�[�R�[�h�͓ǂ݂Ƃ΂�(������JIS�G�X�P�[�v�R�[�h�͂Ƃ΂��Ȃ�)
		int ch;
		do {
			ch = bis.read();
		} while ((ch <= 0x7f && ch != -1) && ch != 0x1b);

		int noOfNonASCIIBytes = 0;
		int[] nonASCIIBytes = new int[fBytesForCheck];	// �ǂݍ��񂾃o�C�g��̂�����ASCII�o�C�g���i�[�����
		while (ch != -1 && noOfNonASCIIBytes < fBytesForCheck) {
			nonASCIIBytes[noOfNonASCIIBytes] = ch;
			noOfNonASCIIBytes++;
			ch = bis.read();
		}
		if (noOfNonASCIIBytes == 0) {	// �o�C�g���ASCII�R�[�h�������琬��
			return ASCII;
		}

		boolean isFirstChar = true;
		int noOfKanjiBytes = 0;
		int[] kanjiBytes = new int[noOfNonASCIIBytes];	// ��ASCII�o�C�g���犿���݂̂����o�������̂��i�[�����
		for (int i = 0; noOfKanjiBytes < noOfNonASCIIBytes && i < noOfNonASCIIBytes; i++) {
			if (isFirstChar && ((nonASCIIBytes[i] <= 0x7f) && (nonASCIIBytes[i] != 0x1b))) {
				continue;
			}
			if (nonASCIIBytes[i] == 0x1b) { // JIS�G�X�P�[�v�R�[�h�����t��������JIS�Ɣ��肵�ďI���
				return JIS;
			}
			if (isFirstChar && (0x80 <= nonASCIIBytes[i]) && (nonASCIIBytes[i] <= 0x9f)) {
				// �ŏ��ɏo���킵����ASCII�o�C�g�����͈̔͂Ȃ�SJIS�Ɣ���
				return SJIS;
			}
			if (isFirstChar
					&& (((0xa1 <= nonASCIIBytes[i]) && (nonASCIIBytes[i] < 0xe0))
							|| ((0xfc < nonASCIIBytes[i]) && (nonASCIIBytes[i] < 0xff)))) {
				// �ŏ��ɏo���킵����ASCII�o�C�g�����͈̔͂Ȃ�EUC�Ɣ���
				return EUC;
			} else {
				kanjiBytes[noOfKanjiBytes] = nonASCIIBytes[i];
				noOfKanjiBytes++;
				isFirstChar = false;
			}
		}

		// UTF-8: [00-7F] or [c0-df][80-bf] or [e0-ef][80-bf][80-bf]
		// �ŏ��̊����̑�1�o�C�g�͂��łɃ`�F�b�N����Ă���D
		// UTF8�Ȃ�Ύ���for����break���Ȃ��͂��D
		boolean isUTF = false;
		for (int i = 0; i < noOfKanjiBytes - 2; ) {
			if (0xe0 <= kanjiBytes[i]&& kanjiBytes[i] <= 0xef) {
				if (0x80 <= kanjiBytes[i + 1] && kanjiBytes[i + 1] <= 0xbf) {
					if (0x80 <= kanjiBytes[i + 2]&& kanjiBytes[i + 2] <= 0xbf) {
						isUTF = true; // UTF 3�o�C�g�p�^�[��
						i += 3;
					} else {
						isUTF = false;
						break;
					}
				} else {
					isUTF = false;
					break;
				}
			} else if (0xc0 <= kanjiBytes[i]&& kanjiBytes[i] <= 0xdf) {
				if (0x80 <= kanjiBytes[i + 1] && kanjiBytes[i + 1] <= 0xbf) {
					isUTF = true; // UTF 2�o�C�g�p�^�[��
					i += 2;
				} else {
					isUTF = false;
					break;
				}
			} else {
				i += 1;
			}
		}
		if (isUTF) {
			return UTF8;
		}

		// �ȉ�UTF�łȂ��C�]���Ċ�����SJIS�܂���EUC��2�o�C�g�����Ƃ��đ�1,2�o�C�g������S�ă`�F�b�N����.
		// ���x��SJIS�܂���EUC�łȂ��������݂Ă���
		// EUC: [a1-ff][a1-ff]
		boolean isEUC = true;
		for (int i = 0; i < noOfKanjiBytes; ) {
			if (kanjiBytes[i] <= 0x7f) {
				i += 1;
				continue;
			}
			if ((kanjiBytes[i] <= 0x9f) || (kanjiBytes[i + 1] <= 0x9f)) {
				isEUC = false;
				break;
			} else {
				i += 2;
			}
		}
		// SJIS��1�o�C�g: [80-9f], [e0-ea], [ed-fc]
		// SJIS��2�o�C�g: [40-fe]
		boolean isSJIS = true;
		for (int i = 0; i < noOfKanjiBytes; ) {
			if (kanjiBytes[i] <= 0x7f) {
				i += 1;
				continue;
			}
			if ((0xa1 <= kanjiBytes[i] && kanjiBytes[i] < 0xe0)
					|| (kanjiBytes[i] > 0xfc)) {
				isSJIS = false;
				break;
			} else {
				i += 2;
			}
		}

		// ���������͊m���I���f�ƂȂ�܂����C�ʏ�̓��{�ꕶ���͕K�������Ȃ��ӂ��݁C
		// �����Ȃ�SJIS�R�[�h��EUC�R�[�h�Ƃ͏d�����܂��񂩂�C�uEUC�łȂ��v�Ƃ������f�͂��Ȃ�m���ɂł��܂��D
		if (!isEUC) {	// EUC�łȂ����f��D��
			return SJIS;
		} else if (!isSJIS) {
			return EUC;	
		} else if (isEUC && isSJIS) {
			return EUC;	// SJIS��񐅏��������d�����Ă��邪����ɍ���قǑ�񐅏������o���邱�Ƃ͂Ȃ����낤
		} else {
			return UNKNOWN;
		}		
	}

	/**
	 * �t�@�C���̕����R�[�h�𔻒肵�C���肳�ꂽ�R�[�h�̕����Z�b�g��Ԃ��D
	 * ���ʂł��镶���R�[�h��SJIS, EUC, UTF8, JIS, ASCII�ł���C
	 * ����ȊO�̕����R�[�h���Ɣ��肳�ꂽ�ꍇ�ɂ�null��Ԃ��D
	 * @param filename �����R�[�h�̔�����󂯂�t�@�C���̃p�X�D
	 * @return �t�@�C���̃G���R�[�f�B���O�Ɏg���Ă��镶���Z�b�g�D
	 * @throws FileNotFoundException �t�@�C��filename�����݂��Ȃ��Ƃ��D
	 * @throws IOException �t�@�C���̓ǂݍ��ݒ��ɃG���[�����������Ƃ��D
	 * @since 2 hmkz
	 */
	public Charset detectCharset(String filename) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(filename);
		Charset result = detectCharset(fis);
		fis.close();
		return result;
	}

}
