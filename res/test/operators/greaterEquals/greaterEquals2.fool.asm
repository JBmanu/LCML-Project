push 0
push 4
push 4
bleq label2
push 0
b label3
label2:
push 1
label3:
lfp
push -2
add
lw
print
halt