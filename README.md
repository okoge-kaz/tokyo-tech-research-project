# Tokyo-Tech-Research-Project

東工大 学部 3 年次 研究プロジェクト(小野研)

Tokyo Institute of Technology Undergraduate 3rd year Research Project (ONO Lab)

Genetic Algorithm for non-linear optimization

## 実行環境 (Running Environment)

研究プロジェクトではEclipse を用いることが推奨されている。

だがEclipse のショートカットと Visual Studio Code のショートカットが異なっており開発体験が悪いため、Visual Studio Code を用いることにした。

以下では、Visual Studio Code を用いた開発環境の構築方法を記す。

まず小野先生も指摘していたが、Visual Studio Code では複数プロジェクトを同時に開くことができない。（原則）
そのため、ResearchProject01, ResearchProject02, ... のようにプロジェクトごとに別々の Visual Studio Code ワークスペースで開いて開発を行う。

上記のように、1-Java Project, 1-VSCode Workspace という構成にすれば、簡単に実行環境を用意することが可能である。

私が導入した Java 関連のプラグインは以下の通りである。

![](public/vscode-extensions.png)

これらの拡張機能(プラグイン)を導入すれば、main 関数の横に`Run|Debug`のような表示が出現する。
これを用いれば、簡単に実行環境を用意することができる。

また、デバッグ機能も特別な設定をせずに用いることができる。

かなり強力なデバッガであるため、積極的に持ちることを推奨する。

## ディレクトリ構成 (Directory Structure)

```
tokyo-tech-research-project/
├── README.md
├── ResearchProject01
│   ├── bin
│   │   ├── crfmnes
│   │   ├── jgoal
│   │   ├── jssf
│   │   ├── report01
│   │   ├── report02
│   │   ├── report03
│   │   ├── report04
│   │   ├── report05
│   │   ├── report06
│   │   └── samples
│   └── src
│       ├── crfmnes
│       ├── jgoal
│       ├── jssf
│       ├── report01
│       ├── report02
│       ├── report03
│       ├── report04
│       ├── report05
│       ├── report06
│       └── samples
├── ResearchProject02
│   ├── bin
│   │   └── rexjgg
│   └── src
│       └── rexjgg
├── ResearchProjectLensDesign
│   ├── 3lens-F3_0-f100-w19.txt
│   ├── GaussLens.txt
│   ├── NoEDGlass.glass
│   ├── bin
│   │   └── lensDesignProblem
│   ├── gauss-mod.glass
│   └── src
│       └── lensDesignProblem
└── public
    └── vscode-extensions.png
```

- ResearchProject01

  初回の講義で配布される講義資料

  遺伝アルゴリズムを用いた最適化問題の解法を実感するためのコードがある。
  詳しい仕組みなどは、一緒に配布される論文などを読むことわかる。

- ResearchProject02

  第２回, 第 3 回の講義で用いるコード。

  ここで REX/JGG を自前で実装してみることが要求される。

  実装自体は、配布される手順書通りに行えば問題ないが、１点注意が必要である。
  それは、私がハマった点でもあるのだが、Java の参照型についての挙動である。

  REX/JGG を実装するにあたって、Sort をする機会がある。そこで java.util.ArrayList を用いることを検討すると思われるが、ここにはまりどころがある。

  それは、shallow copy, deep copy についてである。

  [参考記事](https://qiita.com/KuwaK/items/255de3454ea3327211d3)

  [ArrayList deepcopy](https://codechacha.com/ja/java-arraylist-deep-copy/)
