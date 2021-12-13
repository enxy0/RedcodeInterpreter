# Redcode Interpreter
>Redcode '94 Interpreter built with Kotlin and Jetpack Compose Desktop.
> 
**Supported Addressing modes:**
* `#` - Immediate
* `$` - Direct
* `@` - B-field indirect
* `<` - B-field indirect with predecrement
* `>` - B-field indirect with postincrement

**Supported Commands**:
* `DAT` - data (kills the process)
* `MOV` - move (copies data from one address to another)
* `ADD` - add (adds one number to another)
* `SUB` - subtract (subtracts one number from another)
* `MUL` - multiply (multiplies one number with another)
* `DIV` - divide (divides one number with another)
* `MOD` - modulus (divides one number with another and gives the remainder)
* `JMP` - jump (continues execution from another address)
* `JMZ` - jump if zero (tests a number and jumps to an address if it's 0)
* `JMN` - jump if not zero (tests a number and jumps if it isn't 0)
* `DJN` - decrement and jump if not zero (decrements a number by one, and jumps unless the result is 0)
* `CMP` - skip if equal (compares two instructions, and skips the next instruction if they are equal)
* `SLT` - skip if lower than (compares two values, and skips the next instruction if the first is lower than the second)

## Built with
* [Kotlin](https://kotlinlang.org/)
* [Jetpack Compose Desktop](https://github.com/JetBrains/compose-jb)

## Screenshots
<img src="https://raw.githubusercontent.com/enxy0/RedcodeInterpreter/master/.github/main.png?token=AID63I5C2B25QM5DDXGLY7TBXPP5G">
