push 0
push 3
push 1
sub
push 2
div
push 1
push 4
push 5
bleq label2
push 1
b f
label2:
push 4
push 5
beq label3
push 0
b f
label3:
push 1
f :
push 1
beq label0
push 0
b label1
label0:
push 1
label1:
print
halt