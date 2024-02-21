push 0
push 3
push 1
sub
push 1
lfp
push -2
add
lw
push 1
beq label4
push 0
b label5
label4:
push 1
label5:
push 1
beq label2
lfp
push -2
add
lw
push 10
push 1
sub
bleq label6
push 1
b label7
label6:
push 0
label7:
push 1
beq label2
push 0
b label3
label2:
push 1
label3:
push 1
beq label0
push 0
b label1
label0:
push 1
label1:
print
halt