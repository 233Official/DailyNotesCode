from PIL import Image
import sys
from pathlib import Path


def convert_to_ico(input_file):
    """将输入图片转换成 tauri 所需的如下格式的图标文件：
    128x128.png		Square142x142Logo.png	Square310x310Logo.png	StoreLogo.png
    128x128@2x.png		Square150x150Logo.png	Square44x44Logo.png	icon.icns
    32x32.png		Square284x284Logo.png	Square71x71Logo.png	icon.ico
    Square107x107Logo.png	Square30x30Logo.png	Square89x89Logo.png	icon.png
    """
    # 首先加载图像并确保它是RGBA模式
    img = Image.open(input_file)
    if img.mode != 'RGBA':
        print(f"转换原始图片到RGBA模式")
        img = img.convert('RGBA')
    
    CURRENT_DIR = Path(__file__).resolve().parent
    OUTPUT_DIR = CURRENT_DIR / 'out'
    OUTPUT_DIR.mkdir(exist_ok=True)
    
    # 定义需要生成的尺寸
    sizes = {
        "32x32.png": (32, 32),
        "128x128.png": (128, 128),
        "128x128@2x.png": (256, 256),
        "Square30x30Logo.png": (30, 30),
        "Square44x44Logo.png": (44, 44),
        "Square71x71Logo.png": (71, 71),
        "Square89x89Logo.png": (89, 89),
        "Square107x107Logo.png": (107, 107),
        "Square142x142Logo.png": (142, 142),
        "Square150x150Logo.png": (150, 150),
        "Square284x284Logo.png": (284, 284),
        "Square310x310Logo.png": (310, 310),
        "StoreLogo.png": (50, 50),
        "icon.png": (512, 512),
    }
    
    # 生成各种尺寸的PNG图标
    for name, size in sizes.items():
        resized_img = img.copy()
        resized_img = resized_img.resize(size, Image.LANCZOS)
        # 确保每个图像都是RGBA模式
        if resized_img.mode != 'RGBA':
            resized_img = resized_img.convert('RGBA')
        output_path = OUTPUT_DIR / name
        resized_img.save(output_path, format="PNG")
        print(f"已生成 {name}，模式: {resized_img.mode}")
    
    # 生成ICO文件（Windows图标）
    ico_sizes = [(16, 16), (32, 32), (48, 48), (64, 64), (128, 128)]
    icon_path = OUTPUT_DIR / "icon.ico"
    
    # 尝试使用另一种方法生成ICO
    ico_images = []
    for size in ico_sizes:
        ico_img = img.copy()
        ico_img = ico_img.resize(size, Image.LANCZOS)
        if ico_img.mode != 'RGBA':
            ico_img = ico_img.convert('RGBA')
        ico_images.append(ico_img)
    
    # 保存多尺寸ICO
    ico_images[0].save(icon_path, format="ICO", sizes=[(ico_images[i].width, ico_images[i].height) for i in range(len(ico_images))], 
                      append_images=ico_images[1:])
    print("已生成 icon.ico")
    
    # 生成ICNS文件（macOS图标）
    icns_dir = OUTPUT_DIR / "icon.iconset"
    icns_dir.mkdir(exist_ok=True)
    
    icns_sizes = {
        "16x16": (16, 16),
        "32x32": (32, 32),
        "64x64": (64, 64),
        "128x128": (128, 128),
        "256x256": (256, 256),
        "512x512": (512, 512),
    }
    
    # 为每个尺寸创建正确格式的文件名
    for name, size in icns_sizes.items():
        # 创建标准尺寸的图标
        resized_img = img.copy()
        resized_img = resized_img.resize(size, Image.LANCZOS)
        if resized_img.mode != 'RGBA':
            resized_img = resized_img.convert('RGBA')
        
        # 使用正确的iconset文件名格式 (例如: icon_16x16.png)
        icon_file = icns_dir / f"icon_{name}.png"
        resized_img.save(icon_file, format="PNG")
        print(f"已生成 icon_{name}.png，模式: {resized_img.mode}")
        
        # 生成@2x版本
        if size[0] <= 512:  # 只为可以生成2倍尺寸的图标生成@2x版本
            double_size = (size[0] * 2, size[1] * 2)
            double_img = img.copy()
            double_img = double_img.resize(double_size, Image.LANCZOS)
            if double_img.mode != 'RGBA':
                double_img = double_img.convert('RGBA')
            
            # 使用正确的@2x命名格式 (例如: icon_16x16@2x.png)
            double_icon_file = icns_dir / f"icon_{name}@2x.png"
            double_img.save(double_icon_file, format="PNG")
            print(f"已生成 icon_{name}@2x.png，模式: {double_img.mode}")
    
    print(f"已生成 iconset 文件夹: {icns_dir}")
    print("要生成 ICNS 文件，请在 macOS 终端中运行:")
    print(f"iconutil -c icns {icns_dir} -o {OUTPUT_DIR}/icon.icns")
    
    # 为方便查看结果，打印图像模式信息
    print("\n检查图像模式:")
    for png_file in icns_dir.glob("*.png"):
        try:
            img_check = Image.open(png_file)
            print(f"{png_file.name}: {img_check.mode}")
        except Exception as e:
            print(f"检查 {png_file.name} 失败: {str(e)}")


def main():
    CURRENT_DIR = Path(__file__).resolve().parent
    input_file = CURRENT_DIR / 'input/shiki.jpg'
    convert_to_ico(input_file=input_file)


if __name__ == "__main__":
    main()

