# 白小姐计算器 Android 项目

- 最新数据接口：`https://macaumarksix.com/api/macaujc2.com`
- 历史数据接口：`https://history.macaumarksix.com/history/macaujc2/y/{year}`
- 位置轨迹：3→4→5→6→7→6→5→4，循环重复。
- 号码运算：`((n - 1 + 3) % 49) + 1`
- 生肖序列按用户指定从马开始：马、蛇、龙、兔、虎、牛、鼠、猪、狗、鸡、猴、羊，01号作为序列起点。此映射应以用户实际使用的生肖号码规则再核验。

## 手机云端编译
项目内含 GitHub Actions 工作流 `.github/workflows/build-apk.yml`。上传整个项目到 GitHub 仓库后，在仓库 Actions 页面运行 `Build Android APK`，完成后下载名为 `macau-marksix-debug-apk` 的 artifact，解压得到 `app-debug.apk`。

## 注意
第三方开奖接口可能调整数据结构或不可用；生肖号码表也可能存在用户自定义起点。本项目是初版，需要联网实测。APK 当前未在本环境编译验证。
