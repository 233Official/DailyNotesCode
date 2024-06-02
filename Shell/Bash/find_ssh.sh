#! /bin/bash
if [ ! -d "res" ]; then
    mkdir res
fi

files=$(
    grep -rli "BEGIN RSA PRIVATE KEY\|BEGIN DSA PRIVATE KEY\|BEGIN OPENSSH PRIVATE KEY" /etc/ssh /root /home --exclude=*.{jar,py,pyc,js} --binary-files=without-match
)

echo $files >res/file_name.txt

for file in $files; do
    cp $file res/
done


echo "Done!"
