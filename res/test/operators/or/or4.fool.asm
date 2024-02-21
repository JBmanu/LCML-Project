push 0
push 1
push 1
beq label20
push 1
push 1
beq label20
push 0
b label21
label20:
push 1
label21:
lfp
push -2
add
lw
print
halt