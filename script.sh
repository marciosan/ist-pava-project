#!/usr/bin/fish

# dir containing outA.txt and others
set OUTS_DIR tests

for i in (find src/ist/meic/pa/tests/ | grep "Test" | grep -Eo '.\.' | grep -Eo '[^.]*' | sort)
	java -jar bin/classes/keyConstructors.jar Test$i > /tmp/$i.out
	diff /tmp/$i.out $OUTS_DIR/out$i.txt
	echo $i
end
