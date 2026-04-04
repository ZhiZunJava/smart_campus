---
description: 自动提交并推送代码到 GitHub
---

请执行以下 Git 提交流程：

1. 运行 `git status --short` 查看所有变更文件
2. 运行 `git diff --stat` 查看变更统计
3. 根据变更内容，自动生成一条中文 commit message，格式为：
   - 第一行：类型前缀(feat/fix/refactor/style/docs/chore) + 简短描述（不超过50字）
   - 空行
   - 详细变更列表，每条以 `- ` 开头
4. 执行 `git add -A`
5. 执行 `git commit -m "生成的commit message"`
6. 执行 `git push`
7. 输出提交结果摘要

如果用户提供了额外参数 `$ARGUMENTS`，将其作为 commit message 的主题。

注意：
- 如果没有任何变更，提示"没有需要提交的变更"并停止
- 如果 push 失败，提示用户并显示错误信息
- commit message 应准确反映实际变更内容
