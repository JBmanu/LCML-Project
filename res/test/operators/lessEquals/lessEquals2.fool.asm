push 0
push 4
push 5
bleq label12
push 0
b label13
label12:
push 1
label13:
lfp
push -2
add
lw
print
halt