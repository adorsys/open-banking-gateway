for i in $(grep "^#" dictionary.md | awk '{print $NF}'); do echo "- [$i](dictionary.md#$i)"; done | sort -f
