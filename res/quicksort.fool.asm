push 0
lhp
push class0method0
lhp
sw
lhp
push 1
add
shp
push class0method1
lhp
sw
lhp
push 1
add
shp
push function1
push function2
push function4
push function5
/* newNode*/
push 2
/* newNode*/
push 1
/* newNode*/
push -4
/* newNode*/
push 3
/* newNode*/
push 2
/* newNode*/
push 5
push -1
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
lfp
lfp
lfp
push -7
add
lw
lfp
stm
ltm
ltm
push -6
add
lw
js
lfp
stm
ltm
ltm
push -3
add
lw
js
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

class0method1:
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

function0:
cfp
lra
/* newNode*/
lfp
push 2
add
lw
lfp
push 1
add
lw
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
stm
sra
pop
pop
pop
sfp
ltm
lra
js

function1:
cfp
lra
push function0
lfp
push 1
add
lw
push -1
beq label2
push 0
b label3
label2:
push 1
label3:
push 1
beq label0
lfp
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
print
lfp
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
lfp
lw
stm
ltm
ltm
push -3
add
lw
js
lfp
stm
ltm
ltm
push -2
add
lw
js
b label1
label0:
push -1
label1:
stm
pop
sra
pop
pop
sfp
ltm
lra
js

function2:
cfp
lra
lfp
push 1
add
lw
push -1
beq label6
push 0
b label7
label6:
push 1
label7:
push 1
beq label4
/* newNode*/
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
lfp
lfp
push 2
add
lw
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
lfp
lw
stm
ltm
ltm
push -4
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
b label5
label4:
lfp
push 2
add
lw
label5:
stm
sra
pop
pop
pop
sfp
ltm
lra
js

function3:
cfp
lra
lfp
lw
push 3
add
lw
push 1
beq label8
lfp
push 1
add
lw
push 0
beq label10
push 0
b label11
label10:
push 1
label11:
b label9
label8:
lfp
push 1
add
lw
label9:
stm
sra
pop
pop
sfp
ltm
lra
js

function4:
cfp
lra
push function3
lfp
push 1
add
lw
push -1
beq label14
push 0
b label15
label14:
push 1
label15:
push 1
beq label12
lfp
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
lfp
push 2
add
lw
bleq label18
push 0
b label19
label18:
push 1
label19:
lfp
stm
ltm
ltm
push -2
add
lw
js
push 1
beq label16
lfp
lfp
push 3
add
lw
lfp
push 2
add
lw
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
lfp
lw
stm
ltm
ltm
push -5
add
lw
js
b label17
label16:
/* newNode*/
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
lfp
lfp
push 3
add
lw
lfp
push 2
add
lw
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
lfp
lw
stm
ltm
ltm
push -5
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
label17:
b label13
label12:
push -1
label13:
stm
pop
sra
pop
pop
pop
pop
sfp
ltm
lra
js

function5:
cfp
lra
lfp
push 1
add
lw
push -1
beq label22
push 0
b label23
label22:
push 1
label23:
push 1
beq label20
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
b label21
label20:
push 0
label21:
lfp
push 1
add
lw
push -1
beq label26
push 0
b label27
label26:
push 1
label27:
push 1
beq label24
lfp
/* newNode*/
lfp
push -2
add
lw
lfp
lfp
push 0
lfp
push -2
add
lw
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
lfp
lw
stm
ltm
ltm
push -5
add
lw
js
lfp
lw
stm
ltm
ltm
push -6
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
lfp
lfp
push 1
lfp
push -2
add
lw
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
lfp
lw
stm
ltm
ltm
push -5
add
lw
js
lfp
lw
stm
ltm
ltm
push -6
add
lw
js
lfp
lw
stm
ltm
ltm
push -4
add
lw
js
b label25
label24:
push -1
label25:
stm
pop
sra
pop
pop
sfp
ltm
lra
js