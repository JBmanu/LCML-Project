push 0
push 1
push 5
add
push 1
lfp
push -2
add
lw
push 5
beq label2
push 0
b label3
label2:
push 1
label3:
push 1
beq label0
lfp
push -2
add
lw
push 2
add
b label1
label0:
lfp
push -2
add
lw
push 1
add
label1:
print
halt