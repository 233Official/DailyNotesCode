#!/usr/bin/env bash
# Bash 版本的 Folo OPML 订阅转换脚本, 依赖 Perl 完成 XML 属性替换。

set -euo pipefail

print_help() {
  cat <<'EOF'
用法: ./folo_opml_resolver.sh -url <RSSHub地址> -key <ACCESS_KEY> -file <OPML文件> [-out <输出文件>]

参数说明:
  -url,  --rsshub_url        自部署 RSSHub 的基础地址 (需包含 http/https)
  -key,  --access_key        RSSHub 部署时设置的 ACCESS_KEY
  -file, --opml_filepath     Folo 导出的 OPML 文件路径
  -out,  --output_filepath   输出 OPML 文件, 默认在同目录生成 *_resolved.opml
  -h,    --help              查看帮助

说明: 本脚本使用 Perl 处理 OPML, macOS/Linux 默认均内置 Perl, 可直接运行。
EOF
}

die() {
  echo "错误: $1" >&2
  exit 1
}

fetch_value() {
  local name="$1"
  shift
  [[ $# -gt 0 ]] || die "参数 ${name} 缺少取值"
  printf '%s' "$1"
}

rsshub_url=""
access_key=""
opml_file=""
output_file=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    -url|--rsshub_url)
      shift
      rsshub_url="$(fetch_value --rsshub_url "$@")"
      shift
      ;;
    -key|--access_key)
      shift
      access_key="$(fetch_value --access_key "$@")"
      shift
      ;;
    -file|--opml_filepath)
      shift
      opml_file="$(fetch_value --opml_filepath "$@")"
      shift
      ;;
    -out|--output_filepath)
      shift
      output_file="$(fetch_value --output_filepath "$@")"
      shift
      ;;
    -h|--help)
      print_help
      exit 0
      ;;
    *)
      die "未知参数 $1"
      ;;
  esac
done

[[ -n "$rsshub_url" ]] || die "必须提供 -url/--rsshub_url"
[[ -n "$access_key" ]] || die "必须提供 -key/--access_key"
[[ -n "$opml_file" ]] || die "必须提供 -file/--opml_filepath"

[[ -f "$opml_file" ]] || die "找不到 OPML 文件: $opml_file"

if [[ ! "$rsshub_url" =~ ^https?:// ]]; then
  die "RSSHub 地址必须以 http:// 或 https:// 开头"
fi
if [[ "$rsshub_url" == *\?* || "$rsshub_url" == *\#* ]]; then
  die "RSSHub 基础地址不应包含查询参数或片段"
fi

rsshub_url="${rsshub_url%/}"

if [[ -z "$output_file" ]]; then
  dir_name=$(dirname "$opml_file")
  base_name=$(basename "$opml_file")
  if [[ "$base_name" == *.* ]]; then
    stem="${base_name%.*}"
    ext=".${base_name##*.}"
  else
    stem="$base_name"
    ext=".opml"
  fi
  output_file="${dir_name}/${stem}_resolved${ext}"
fi

output_dir=$(dirname "$output_file")
[[ -d "$output_dir" ]] || die "输出目录不存在: $output_dir"

perl - "$rsshub_url" "$access_key" "$opml_file" "$output_file" <<'PERL'
use strict;
use warnings;
use Encode qw(decode encode);

my ($base, $key, $input_path, $output_path) = @ARGV;
open my $in_fh, "<:raw", $input_path or die "无法读取 OPML 文件 $input_path: $!";
local $/;
my $raw = <$in_fh>;
close $in_fh;
my $content = decode("UTF-8", $raw);

my $count = 0;
$content =~ s{xmlUrl="https://rsshub\.app([^"?"]*)(\?[^"]*)?"}{
    my $path = defined $1 ? $1 : '';
    my $query = defined $2 ? $2 : '';
    my $new_url = $base . $path;
    my @params;
    if ($query ne '') {
        $query =~ s/^\?//;
        my @pairs = split /&amp;/, $query;
        for my $entry (@pairs) {
            next unless length $entry;
            my ($name) = split /=/, $entry, 2;
            next if defined $name && $name eq 'key';
            push @params, $entry;
        }
    }
    push @params, "key=$key";
    my $query_str = join('&amp;', @params);
    $new_url .= '?' . $query_str if length $query_str;
    $count++;
    qq{xmlUrl="$new_url"};
}ge;

if ($count) {
    open my $out_fh, ">:raw", $output_path or die "无法写入 OPML 文件 $output_path: $!";
    print {$out_fh} encode("UTF-8", $content);
    close $out_fh;
    print "已在 $output_path 写入 $count 条订阅。\n";
} else {
    print "未发现以 https://rsshub.app 开头的订阅, 原文件保持不变。\n";
}
PERL
