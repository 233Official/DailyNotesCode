# 生成一个长度为 5 的, 由大小写字母和数字组成的随机密码
import random
import string


def random_password():
    return "".join(random.sample(string.ascii_letters + string.digits, 5))


def main():
    print(random_password())


if __name__ == "__main__":
    main()
