package com.challenge.fidoreader.Util

// 필요없어 보임
enum class RetStatusWord(private var szSWStr: String, private var szDescriptionStr: String){
    SW001("9000", "Successful"),
    SW002("6200","No information given"),
    SW003("6281","Part of returned data may be corrupted"),
    SW004("6282","End of file/record reached before reading Le bytes"),
    SW005("6283","Selected file invalidated"),
    SW006("6284","FCI not formatted according to 1.1.5"),
    SW007("6300","Authentication failed"),
    SW008("6381","File filled up by the last write"),
    SW009("6400","State of nonvolatile memory unchanged"),
    SW010("6500","No information given"),
    SW011("6581","Memory failure"),
    SW012("6700","Wrong length"),
    SW013("6800","No information given"),
    SW014("6881","Logical channel not supported"),
    SW015("6882","Secure messaging not supported"),
    SW016("6900","No information given"),
    SW017("6981","Command incompatible with file structure"),
    SW018("6982","Security status not satisfied"),
    SW019("6983","Authentication method blocked"),
    SW020("6984","Reference data invalidated"),
    SW021("6985","Conditions of use not satisfied"),
    SW022("6986","Command not allowed"),
    SW023("6987","Secure messaging data object missing"),
    SW024("6988","Secure messaging data object incorrect"),
    SW025("6A00","No information given"),
    SW026("6A80","Incorrect parameters in the data field"),
    SW027("6A81","Function not supported"),
    SW028("6A82","File not found"),
    SW029("6A83","Record not found"),
    SW030("6A84","Not enough memory space in file"),
    SW031("6A85","Lc inconsistent with TLV structure"),
    SW032("6A86","Incorrect parameters P1 P2"),
    SW033("6A87","Referenced data not found"),
    SW034("6D00","Instruction code not supported or invalid"),
    SW035("6E00","Class not supported"),
    SW036("6F00","No precise diagnostics");

    private fun getDesdcriptionStr(): String{
        return szDescriptionStr
    }

    private fun getSWStr(): String{
        return szSWStr
    }

    companion object{
        fun getSWDescription(swStr: String): String {
            var retStatusWords: Array<RetStatusWord> = RetStatusWord.values()

            for(ii in retStatusWords.indices){
                var retStatusWord = retStatusWords[ii]

                if(retStatusWord.getSWStr().equals(swStr, true)){
                    return retStatusWord.getDesdcriptionStr()
                }
            }
            return ""
        }
    }

}