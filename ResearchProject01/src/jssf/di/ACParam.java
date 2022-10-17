package jssf.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * �R���X�g���N�^�̈�����ݒ�t�@�C���̃L�[�Ɗ֘A�t����D
 * ���̃A�m�e�[�V�����̓R���X�g���N�^�̈����ɕt�������D
 * ���ׂĂ̈����ɂ��̃A�m�e�[�V�������t�����ꂽ�R���X�g���N�^�����N���X�́C
 * {@link TCObjectFactory#create(String)}�Ȃǂ�p���ăC���X�^���X�̐������\�ƂȂ�D
 *
 * @since 2
 * @author hmkz
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ACParam {
	/**
	 * �ϐ�����Ԃ�
	 * @return �ϐ���
	 * @since 2 hmkz
	 */
	String key();

	/**
	 * �f�t�H���g�l��Ԃ�
	 * @return �f�t�H���g�l
	 * @since 2 hmkz
	 */
	String defaultValue() default "";

}
