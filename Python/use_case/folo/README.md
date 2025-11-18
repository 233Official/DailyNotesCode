# Folo

---

## Folo 导出 OPML 订阅转换器

- `需求背景`: 当前 Folo 导出的 OPML 可以自定义 RSSHub URL 替换为自部署的 URL, 但是尚未支持自定义 ACCESS_KEY, 在 Folo 实装此功能前可以通过此脚本实现
- `效果`: 将 Folo 导出的 OPML 订阅源中的 `https://rsshub.app` 替换成自部署的 URL 并添加自定义 Key

---

## 使用方式

在 Folo 中导出订阅源(不要填写 RSSHub URL):

![image-20251118113546886](http://cdn.ayusummer233.top/DailyNotes/202511181135434.png)

![image-20251118133607702](http://cdn.ayusummer233.top/DailyNotes/202511181336916.png)

### Python 版本

运行 `folo_opml_resolver.py` 并传入必要参数:
- `--rsshub_url/-url`: 自部署 RSSHub 的基础地址，例如 `http://rsshub.self.top:8080`
- `--access_key/-key`: RSSHub 部署时配置的 `ACCESS_KEY`
- `--opml_filepath/-file`: 需要处理的 OPML 文件路径
- `--output_filepath/-out` (可选): 输出 OPML 路径，不提供时会在输入文件同目录生成 `原名_resolved.opml`

示例:

```bash
python folo_opml_resolver.py \
  -url=http://rsshub.self.top:8080 \
  -key=dascwe \
  -file="D:/Downloads/follow.opml" \
  -out="D:/Downloads/follow_custom.opml"
```

### Bash 版本 (macOS/Linux)

- 依赖: Bash + Perl (两者在主流 Linux/macOS 系统中默认存在)。
- 参数名称与 Python 版本完全一致:

```bash
bash folo_opml_resolver.sh \
  -url=http://rsshub.self.top:8080 \
  -key=dascwe \
  -file="~/Downloads/follow.opml"
```

### PowerShell 版本 (Windows/PowerShell Core)

- 依赖: Windows 自带 PowerShell 5+ 或 PowerShell Core (`pwsh`)。

```powershell
pwsh .\folo_opml_resolver.ps1 `
  -url http://rsshub.self.top:8080 `
  -key dascwe `
  -file D:\Downloads\follow.opml
```

三套脚本逻辑一致: 扫描 OPML 中所有 `xmlUrl` 属性，只要以 `https://rsshub.app` 开头就替换为自部署地址并在末尾增加/覆盖 `key=<ACCESS_KEY>`。默认会在同目录生成 `*_resolved.opml` 副本，你也可以传入 `-out/--output_filepath` 自定义输出路径。

---
