push 0
push 3
push 1
sub
push 2
div
push 1
push 5
push 5
bleq label0
push 0
b label1
label0:
push 1
label1:
print
halt