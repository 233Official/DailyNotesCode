import tldextract

# 解析普通域名
extracted = tldextract.extract("www.example.com")
print(f"子域名: {extracted.subdomain}")  # 输出: 子域名: www
print(f"主域名: {extracted.domain}")     # 输出: 主域名: example
print(f"顶级域: {extracted.suffix}")     # 输出: 顶级域: com

# 解析多段式顶级域名
extracted = tldextract.extract("blog.example.co.uk")
print(f"子域名: {extracted.subdomain}")  # 输出: 子域名: blog
print(f"主域名: {extracted.domain}")     # 输出: 主域名: example
print(f"顶级域: {extracted.suffix}")     # 输出: 顶级域: co.uk