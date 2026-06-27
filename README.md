# 扫雷2026

Java Swing 扫雷课程作业，按 MVC 结构组织源码，可使用 JDK 11 编译运行。

## 项目结构

- `src/MineSweeper.java`
  - 程序入口，负责设置系统外观并启动程序。
- `src/MineController.java`
  - 控制器层，负责处理点击事件、计时、重置和界面刷新流程。
- `src/MineBoard.java`
  - 模型层，负责保存棋盘数据、布雷、翻格、插旗和胜负判断。
- `src/MineView.java`
  - 视图层，负责窗口界面、按钮网格和状态栏显示。

## 功能说明

1. 固定高级难度：`16 x 30` 棋盘，`99` 个地雷。
2. 首次点击后再布雷，保证首次点击及周围九宫格安全。
3. 支持左键翻格、右键插旗、空白区域自动展开。
4. 支持计时、胜负判断、笑脸重开。

## 编译运行

### 方式1：直接执行批处理

```bat
build.bat
```

### 方式2：手动编译

```powershell
javac --release 11 -encoding UTF-8 -d out11 src\*.java
jar cfe saolei.jar MineSweeper -C out11 .
java -jar saolei.jar
```

## 说明

- 源码已添加模块级和关键方法注释，便于写软件设计报告。
- 当前项目符合“提供带注释源码、可正常运行、JDK 11 可编译”的基础要求。
