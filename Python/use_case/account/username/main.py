# 随机用户名生成器
import random

def generate_username():
    "生成一个长度为 5-10 的, 由大小写字母和数字组成的随机用户名"
    username = ''
    for i in range(random.randint(5, 10)):
        username += random.choice('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789')
    return username

if __name__ == '__main__':
    print(generate_username())