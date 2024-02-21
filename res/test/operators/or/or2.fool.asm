push 0
push 0
push 1
beq label16
push 1
push 1
beq label16
push 0
b label17
label16:
push 1
label17:
lfp
push -2
add
lw
print
halt