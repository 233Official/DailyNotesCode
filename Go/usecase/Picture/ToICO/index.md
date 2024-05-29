# Picture to ICO

> PS: 在 `Python\use_case\picture\to_ico` 有更好用的 `all_in_one` 脚本, 将所有分辨率整合在一个 ico 中

将`png,jpg,jpeg`图片转换为ICO格式, 使用方法如下(先cd到项目根目录):

```bash
# 安装模块
go mod download
# 将 input.png 转换为 output.ico
go run main.go -i input.png -o output.ico
```

> 如果报错 `invalid go version '1.22.1': must match format 1.xx` 的话可以尝试将 `go.mod` 中的 `1.22.1` 改成你当前 go 的版本

![image-20240429140021631](http://cdn.ayusummer233.top/DailyNotes/image-20240429140021631.png)

默认输出的ICO文件包含了`16x16, 32x32, 48x48, 64x64, 128x128, 256x256` 六种尺寸的图标, 如果需要自定义尺寸, 可以使用`-size`参数, 例如:

```bash
go run main.go -i input.png -o output.ico -size 64
```

![image-20240429140306990](http://cdn.ayusummer233.top/DailyNotes/image-20240429140306990.png)