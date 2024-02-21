push 0
push 1
push 1
beq label18
push 0
push 1
beq label18
push 0
b label19
label18:
push 1
label19:
lfp
push -2
add
lw
print
halt