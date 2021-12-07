object TestUtils {
    fun simpleProgram() = """
        ORG 2
        DAT 0                
        DAT -1          
        MOV #5,<-1 
    """.trimIndent()
}
