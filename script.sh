#!/usr/bin/fish

# dir containing outA.txt and others
set OUTS_DIR tests

for i in (find tests/ | grep "Test" | grep -Eo '.\.' | grep -Eo '[^.]*' | sort)
	java -cp 'keyConstructors.jar:bin/tests' ist.meic.pa.KeyConstructors Test$i ^ /tmp/$i.out
	
	echo -e \n\n $i =============================\n\n
	diff /tmp/$i.out $OUTS_DIR/out$i.txt
	
end
