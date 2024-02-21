push 0
push 0
push 1
beq label6
push 0
push 1
beq label6
push 0
b label7
label6:
push 1
label7:
lfp
push -2
add
lw
print
halt