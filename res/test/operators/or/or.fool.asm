push 0
push 0
push 1
beq label8
push 0
push 1
beq label8
push 0
b label9
label8:
push 1
label9:
lfp
push -2
add
lw
print
halt