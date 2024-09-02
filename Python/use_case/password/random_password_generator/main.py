import random
import string

def generate_random_password(length=12, use_lowercase=True, use_uppercase=True, use_digits=True, use_special=True):
    # 初始化字符集
    characters = ''
    
    if use_lowercase:
        characters += string.ascii_lowercase
    if use_uppercase:
        characters += string.ascii_uppercase
    if use_digits:
        characters += string.digits
    if use_special:
        characters += '!@#$%^&*()-_=+'  # 常用的特殊字符

    # 如果没有选择任何字符集，提示错误
    if not characters:
        raise ValueError("必须至少选择一种字符类型")

    # 生成随机密码
    password = ''.join(random.choice(characters) for _ in range(length))
    
    return password

# 使用示例
password_length = 16  # 设定密码长度
password = generate_random_password(length=password_length, use_lowercase=True, use_uppercase=True, use_digits=True, use_special=False)
print(f"生成的随机密码: {password}")
