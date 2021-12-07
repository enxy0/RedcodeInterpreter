package model

sealed class Address(
    open val number: Int,
    open val name: String
) {
    companion object {
        val regex = "[$<>#@]".toRegex()
    }

    abstract fun copy(number: Int): Address

    override fun toString(): String = "$name$number"

    /**
     * "#" Непосредственная адресация
     */
    class Immediate(override val number: Int) : Address(number, "#") {
        override fun copy(number: Int): Address = Immediate(number)
    }

    /**
     * Прямая адресация
     */
    class Direct(override val number: Int) : Address(number, "$") {
        override fun copy(number: Int): Address = Direct(number)
    }

    /**
     * "@" B-косвенная адресация
     */
    open class BFieldIndirect(override val number: Int, override val name: String = "@") : Address(number, name) {
        /**
         * ">" B-предекрементная адресация
         */
        class WithPostincrement(override val number: Int) : BFieldIndirect(number, ">") {
            override fun copy(number: Int): Address = WithPostincrement(number)
        }

        /**
         * "<" B-предекрементная адресация
         */
        class WithPredecrement(override val number: Int) : BFieldIndirect(number, "<") {
            override fun copy(number: Int): Address = WithPredecrement(number)
        }

        override fun copy(number: Int): Address = BFieldIndirect(number)
    }
}
