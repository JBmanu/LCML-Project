push 0
push 4
push 2
bleq label0
push 0
b label1
label0:
push 1
label1:
lfp
push -2
add
lw
print
halt