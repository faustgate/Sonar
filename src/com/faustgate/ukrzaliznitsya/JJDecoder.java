package com.faustgate.ukrzaliznitsya;

//
// Java version of Python version of the jjdecode function written by Syed Zainudeen
// http://csc.cs.utm.my/syed/images/files/jjdecode/jjdecode.html
//
// +NCR/CRC! [ReVeRsEr] - crackinglandia@gmail.com
// Thanks to Jose Miguel Esparza (@EternalTodo) for the final push to make it work!
//


import android.util.Log;

public class JJDecoder {
    private String encoded_str;


    public JJDecoder(String jj_encoded_data) {
        encoded_str = jj_encoded_data;
    }


    private String clean() {
        return encoded_str.trim();
    }

    private Object[] checkPalindrome(String Str) {
        int startpos = -1;
        int endpos = -1;
        String gv = "";
        int gvl = -1;

        int index = Str.indexOf("\"\'\\\"+\'+\",");

        if (index == 0) {
            startpos = Str.indexOf("$$+\"\\\"\"+") + 8;
            endpos = Str.indexOf("\"\\\"\")())()");
            gv = Str.substring(Str.indexOf("\"\'\\\"+\'+\",") + 9, Str.indexOf("=~[]"));
            gvl = gv.length();
        } else {
            gv = Str.substring(0, Str.indexOf("="));
            gvl = gv.length();
            startpos = Str.indexOf("\"\\\"\"+") + 5;
            endpos = Str.indexOf("\"\\\"\")())()");
        }
        Object[] res = {startpos, endpos, gv, gvl};
        return res;
    }


    public String decode() throws Exception {

        encoded_str = clean();
        Object[] res = checkPalindrome(encoded_str);
        int startpos = (int) res[0];
        int endpos = (int) res[1];
        String gv = (String) res[2];
        int gvl = (int) res[3];

        if (startpos == endpos)
            throw new Exception("No data!");

        String data = encoded_str.substring(startpos, endpos);

        String[] b = {"___+", "__$+", "_$_+", "_$$+", "$__+", "$_$+", "$$_+", "$$$+",
                "$___+", "$__$+", "$_$_+", "$_$$+", "$$__+", "$$_$+", "$$$_+",
                "$$$$+"};

        String str_l = "(![]+\"\")[" + gv + "._$_]+";
        String str_o = gv + "._$+";
        String str_t = gv + ".__+";
        String str_u = gv + "._+";

        String str_hex = gv + ".";

        String str_s = "\"";
        String gvsig = gv + ".";

        String str_quote = "\\\\\\\"";
        String str_slash = "\\\\\\\\";

        String str_lower = "\\\\\"+";
        String str_upper = "\\\\\"+" + gv + "._+";

        String str_end = "\"+";

        String out = "";
        while (!data.equals("")) {
            // l o t u
            if (data.indexOf(str_l) == 0) {
                data = data.substring(str_l.length(), data.length());
                out += 'l';
                continue;
            } else if (data.indexOf(str_o) == 0) {
                data = data.substring(str_o.length(), data.length());
                out += 'o';
                continue;
            } else if (data.indexOf(str_t) == 0) {
                data = data.substring(str_t.length(), data.length());
                out += 't';
                continue;
            } else if (data.indexOf(str_u) == 0) {
                data = data.substring(str_u.length(), data.length());
                out += 'u';
                continue;
            }

            // 0123456789abcprivate String
            if (data.indexOf(str_hex) == 0) {
                data = data.substring(str_hex.length(), data.length());
                for (int i = 0; i < b.length; i++) {
                    if (data.indexOf(b[i]) == 0) {
                        data = data.substring(b[i].length(), data.length());
                        out += Integer.toHexString(i);
                        //out += '%x' % i
                        break;
                    }
                }
                continue;
            }

            // start of s block
            if (data.indexOf(str_s) == 0) {
                data = data.substring(str_s.length(), data.length());
                // check if "R
                if (data.indexOf(str_upper) == 0) {  // r4 n >= 128
                    data = data.substring(str_upper.length(), data.length());  // skip sig
                    String ch_str = "";
                    for (int i = 0; i < 2; i++) {// shouldn't be more than 2 hex chars
                        // gv + "."+b[ c ]
                        if (data.indexOf(gvsig) == 0) {
                            data = data.substring(gvsig.length(), data.length());
                            for (int k = 0; k < b.length; k++) {// for every entry in b
                                if (data.indexOf(b[k]) == 0) {
                                    data = data.substring(b[k].length(), data.length());
                                    ch_str = Integer.toHexString(k);
                                    //ch_str = '%x' % k
                                    break;
                                }
                            }
                        } else
                            break;
                    }

                    out += (char) (Integer.parseInt(ch_str, 16));
                    //out += chr(int(ch_str, 16))
                    continue;
                } else if (data.indexOf(str_lower) == 0) {  // r3 check if "R // n < 128
                    data = data.substring(str_lower.length(), data.length());// skip sig
                    String ch_str = "";
                    String ch_lotux = "";
                    String temp = "";
                    int b_checkR1 = 0;

                    for (int j = 0; j < 3; j++) {  // shouldn't be more than 3 octal chars
                        if (j > 1) {  // lotu check
                            if (data.indexOf(str_l) == 0) {
                                data = data.substring(str_l.length(), data.length());
                                ch_lotux = "l";
                                break;
                            } else if (data.indexOf(str_o) == 0) {
                                data = data.substring(str_o.length(), data.length());
                                ch_lotux = "o";
                                break;
                            } else if (data.indexOf(str_t) == 0) {
                                data = data.substring(str_t.length(), data.length());
                                ch_lotux = "t";
                                break;
                            } else if (data.indexOf(str_u) == 0) {
                                data = data.substring(str_u.length(), data.length());
                                ch_lotux = "u";
                                break;
                            }
                        }

                        // gv + "."+b[ c ]
                        if (data.indexOf(gvsig) == 0) {
                            temp = data.substring(gvsig.length(), data.length());
                            for (int k = 0; k < 8; k++) {// for every entry in b octal
                                if (temp.indexOf(b[k]) == 0) {
                                    // if int(ch_str + str(k), 8) > 128{
                                    if (Integer.parseInt(ch_str + k, 8) > 128) {
                                        b_checkR1 = 1;
                                        break;
                                    }
                                    ch_str += k;
                                    data = data.substring(gvsig.length(), data.length());// skip gvsig
                                    data = data.substring(b[k].length(), data.length());
                                    break;
                                }
                            }

                            if (b_checkR1 == 1) {
                                if (data.indexOf(str_hex) == 0) {  // 0123456789abcprivate String
                                    data = data.substring(str_hex.length(), data.length());
                                    // check every element of hex decode string for a match
                                    for (int i = 0; i < b.length; i++) {
                                        if (data.indexOf(b[i]) == 0) {
                                            data = data.substring(b[i].length(), data.length());
                                            ch_lotux = Integer.toHexString(i);
                                            //ch_lotux = '%x' % i
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        } else
                            break;
                    }

                    out += (char) Integer.parseInt(ch_str, 8) + ch_lotux;
                    //out += chr(int(ch_str, 8)) + ch_lotux
                    continue;
                } else { // "S ----> "SR or "S+
                    // if there is, loop s until R 0r +
                    // if there is no matching s block, throw error

                    int match = 0;
                    int n;

                    // searching for matching pure s block
                    while (true) {
                        n = (int) (data.charAt(0));
                        if (data.indexOf(str_quote) == 0) {
                            data = data.substring(str_quote.length(), data.length());
                            out += "\"";
                            match += 1;
                            continue;
                        } else if (data.indexOf(str_slash) == 0) {
                            data = data.substring(str_slash.length(), data.length());
                            out += "\\\\";
                            match += 1;
                            continue;
                        } else if (data.indexOf(str_end) == 0) {  // reached end off S block ? +
                            if (match == 0)
                                throw new Exception("+ no match S block: " + data);
                            data = data.substring(str_end.length(), data.length());
                            break;// step out of the while loop
                        } else if (data.indexOf(str_upper) == 0) { // r4 reached end off S block ? - check if "R n >= 128
                            if (match == 0)
                                throw new Exception("no match S block n>128: " + data);
                            data = data.substring(str_upper.length(), data.length());// skip sig

                            String ch_str = "";
                            String ch_lotux = "";
                            for (int j = 0; j < 10; j++) {// shouldn't be more than 10 hex chars
                                if (j > 1) {  // lotu check
                                    if (data.indexOf(str_l) == 0) {
                                        data = data.substring(str_l.length(), data.length());
                                        ch_lotux = "l";
                                        break;
                                    } else if (data.indexOf(str_o) == 0) {
                                        data = data.substring(str_o.length(), data.length());
                                        ch_lotux = "o";
                                        break;
                                    } else if (data.indexOf(str_t) == 0) {
                                        data = data.substring(str_t.length(), data.length());
                                        ch_lotux = "t";
                                        break;
                                    } else if (data.indexOf(str_u) == 0) {
                                        data = data.substring(str_u.length(), data.length());
                                        ch_lotux = "u";
                                        break;
                                    }
                                }

                                // gv + "."+b[ c ]
                                if (data.indexOf(gvsig) == 0) {
                                    data = data.substring(gvsig.length(), data.length());// skip gvsig
                                    for (int k = 0; k < b.length; k++) {// for every entry in b
                                        if (data.indexOf(b[k]) == 0) {
                                            data = data.substring(b[k].length(), data.length());
                                            ch_str = Integer.toHexString(k);
                                            //ch_str = '%x' % k
                                            break;
                                        }
                                    }
                                } else
                                    break;
                            }  // done
                            out += (char) Integer.parseInt(ch_str, 16);
                            //out += chr(int(ch_str, 16))
                            break;
                        } // step out of the while loop
                        else if (data.indexOf(str_lower) == 0) {  // r3 check if "R // n < 128
                            if (match == 0)
                                throw new Exception("no match S block n<128: " + data);

                            data = data.substring(str_lower.length(), data.length());// skip sig

                            String ch_str = "";
                            String ch_lotux = "";
                            String temp = "";
                            int b_checkR1 = 0;


                            for (int j = 0; j < 3; j++) {  // shouldn't be more than 3 octal chars
                                if (j > 1) {  // lotu check
                                    if (data.indexOf(str_l) == 0) {
                                        data = data.substring(str_l.length(), data.length());
                                        ch_lotux = "l";
                                        break;
                                    } else if (data.indexOf(str_o) == 0) {
                                        data = data.substring(str_o.length(), data.length());
                                        ch_lotux = "o";
                                        break;
                                    } else if (data.indexOf(str_t) == 0) {
                                        data = data.substring(str_t.length(), data.length());
                                        ch_lotux = "t";
                                        break;
                                    } else if (data.indexOf(str_u) == 0) {
                                        data = data.substring(str_u.length(), data.length());
                                        ch_lotux = "u";
                                        break;
                                    }
                                }


                                // gv + "."+b[ c ]
                                if (data.indexOf(gvsig) == 0) {
                                    temp = data.substring(gvsig.length(), data.length());
                                    for (int k = 0; k < 8; k++) {// for every entry in b octal
                                        if (temp.indexOf(b[k]) == 0) {
                                            // if int(ch_str + str(k), 8) > 128{
                                            if (Integer.parseInt(ch_str + k, 8) > 128) {
                                                b_checkR1 = 1;
                                                break;
                                            }
                                            ch_str += k;
                                            data = data.substring(gvsig.length(), data.length());// skip gvsig
                                            data = data.substring(b[k].length(), data.length());
                                            break;
                                        }
                                    }


                                    if (b_checkR1 == 1) {
                                        if (data.indexOf(str_hex) == 0) {  // 0123456789abcprivate String
                                            data = data.substring(str_hex.length(), data.length());
                                            // check every element of hex decode string for a match
                                            for (int i = 0; i < b.length; i++) {
                                                if (data.indexOf(b[i]) == 0) {
                                                    data = data.substring(b[i].length(), data.length());
                                                    ch_lotux = Integer.toHexString(i);
                                                    //ch_lotux = '%x' % i
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } else
                                    break;
                            }

                            out += (char) Integer.parseInt(ch_str, 8) + ch_lotux;
                            //out += chr(int(ch_str, 8)) + ch_lotux
                            break;
                        } // step out of the while loop
                        else if ((0x21 <= n && n <= 0x2f) || (0x3A <= n && n <= 0x40) || (0x5b <= n && n <= 0x60) || (0x7b <= n && n <= 0x7f)) {
                            out += data.charAt(0);
                            data = data.substring(1, data.length());
                            match += 1;
                        }
                    }
                    continue;
                }
            }
            Log.e("", "No match : " + data);
            break;
        }
        return out;
    }
}
