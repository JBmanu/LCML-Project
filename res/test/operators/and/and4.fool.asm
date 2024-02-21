push 0
push 1
push 0
beq label30
push 1
push 0
beq label30
push 1
b label31
label30:
push 0
label31:
lfp
push -2
add
lw
print
halt