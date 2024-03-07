push 0
lhp
push class0method0
lhp
sw
lhp
push 1
add
shp
lhp
push class0method0
lhp
sw
lhp
push 1
add
shp
push class1method0
lhp
sw
lhp
push 1
add
shp
lhp
push class2method0
lhp
sw
lhp
push 1
add
shp
push class2method1
lhp
sw
lhp
push 1
add
shp
lhp
push class2method0
lhp
sw
lhp
push 1
add
shp
push class3method0
lhp
sw
lhp
push 1
add
shp
/* newNode*/
/* newNode*/
push 50000
push 40000
lhp
sw
lhp
push 1
add
shp
lhp
sw
lhp
push 1
add
shp
push 10000
push -3
add
lw
lhp
sw
lhp
lhp
push 1
add
shp
lhp
sw
lhp
push 1
add
shp
push 10000
push -5
add
lw
lhp
sw
lhp
lhp
push 1
add
shp
/* newNode*/
push 20000
push 5000
lhp
sw
lhp
push 1
add
shp
lhp
sw
lhp
push 1
add
shp
push 10000
push -3
add
lw
lhp
sw
lhp
lhp
push 1
add
shp
lfp
/* ClassCallNode */
lfp
push -7
add
lw
lfp
push -6
add
lw
stm
ltm
ltm
lw
push 1
add
lw
js
lfp
push -8
add
lw
push -1
beq label10
push 0
b label11
label10:
push 1
label11:
push 1
beq label8
lfp
/* ClassCallNode */
lfp
push -8
add
lw
stm
ltm
ltm
lw
push 0
add
lw
js
b label9
label8:
push 0
label9:
print
halt

class0method0:
cfp
lra
/* local declaration code */
/* method body */
lfp
lw
push -1
add
lw
stm
/* removing local declaration */
sra
pop
/* removing parameters */
sfp
ltm
lra
js

class1method0:
cfp
lra
/* local declaration code */
/* method body */
lfp
lw
push -2
add
lw
stm
/* removing local declaration */
sra
pop
/* removing parameters */
sfp
ltm
lra
js

class2method0:
cfp
lra
/* local declaration code */
/* method body */
lfp
lw
push -1
add
lw
stm
/* removing local declaration */
sra
pop
/* removing parameters */
sfp
ltm
lra
js

class2method1:
cfp
lra
/* local declaration code */
/* method body */
lfp
/* ClassCallNode */
lfp
push 1
add
lw
stm
ltm
ltm
lw
push 1
add
lw
js
push 30000
push 1
sub
bleq label2
push 1
b label3
label2:
push 0
label3:
push 1
beq label0
push -1
b label1
label0:
/* newNode*/
lfp
/* ClassCallNode */
lfp
lw
push -1
add
lw
stm
ltm
ltm
lw
push 0
add
lw
js
lhp
sw
lhp
push 1
add
shp
push 10000
push -2
add
lw
lhp
sw
lhp
lhp
push 1
add
shp
label1:
stm
/* removing local declaration */
sra
pop
/* removing parameters */
pop
sfp
ltm
lra
js

class3method0:
cfp
lra
/* local declaration code */
/* method body */
lfp
/* ClassCallNode */
lfp
push 1
add
lw
stm
ltm
ltm
lw
push 0
add
lw
js
push 20000
push 1
sub
bleq label6
push 1
b label7
label6:
push 0
label7:
push 1
beq label4
push -1
b label5
label4:
/* newNode*/
lfp
/* ClassCallNode */
lfp
lw
push -1
add
lw
stm
ltm
ltm
lw
push 0
add
lw
js
lfp
/* ClassCallNode */
lfp
lw
push -1
add
lw
stm
ltm
ltm
lw
push 1
add
lw
js
lhp
sw
lhp
push 1
add
shp
lhp
sw
lhp
push 1
add
shp
push 10000
push -3
add
lw
lhp
sw
lhp
lhp
push 1
add
shp
label5:
stm
/* removing local declaration */
sra
pop
/* removing parameters */
pop
sfp
ltm
lra
js