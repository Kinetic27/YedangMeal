package kr.co.kinetic27.yedang.meal.tools

import net.htmlparser.jericho.Element
import net.htmlparser.jericho.Source

import java.io.IOException
import java.io.InputStream
import java.net.URL

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

/*
 * VERSION 8
 * UPDATE 20180212
 *
 * @author Mir(whdghks913)
 *
 * Use : getDateNew, getKcalNew, getMealNew, getPeopleNew
 * Delete : getDate, getKcal, getMeal, getMonthMeal, getPeople
 */
object MealLibrary {

    /**
     * Version 8 Update
     *
     *
     * 나이스 서버가 https로 바뀜에 따라 서버 보안서를 검증하는 코드가 필요해졌습니다.
     * 항상 true를 반환하는 verify()를 Override하는 HostnameVerifier를 만들었습니다.
     */
    private val hostnameVerifier = HostnameVerifier { _, _ -> true }

    /**
     * getDateNew
     */
    /*
    fun getDateNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                   schulKndScCode: String, schMmealScCode: String): Array<String?> {

        val date = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode)

        return getDateNewSub(date, url)
    }*/

    internal fun getDateNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                            schulKndScCode: String, year: String, month: String, day: String): Array<String?> {

        val date = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + "1"
                + "&schYmd=" + year + "." + month + "." + day)

        return getDateNewSub(date, url)
    }

    private fun getDateNewSub(date: Array<String?>, url: String): Array<String?> {
        var mSource: Source? = null

        try {
            val mUrl = URL(url)

            var mStream: InputStream? = null

            /*
              Version 8 Update

              나이스의 급식 파싱 url이 https로 바뀜에 따라 보안을 검증하는 추가 코드가 필요해졌습니다.
              인증서 정보를 검사하는 코드를 간단하게 추가하였습니다.

              코드를 수정함에 따라 Source의 생성자에 들어가야 하는 인수인 URL을 사용하지 못합니다.
              저는 jericho parser의 Source.java를 보면 생성자에 InputStream을 사용할 수 있다는 사실을 발견했습니다.
             */
            try {
                val urlConnection = mUrl.openConnection() as HttpsURLConnection
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.hostnameVerifier = hostnameVerifier
                mStream = urlConnection.inputStream
                mSource = Source(mStream!!)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (mStream != null) {
                    mStream.close()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        assert(mSource != null)
        mSource!!.fullSequentialParse()
        val table = mSource.getAllElements("table")

        for (i in table.indices) {
            if ((table[i] as Element).getAttributeValue("class") == "tbl_type3") {
                val tr = (table[i] as Element).getAllElements("tr")
                val th = (tr[0] as Element).getAllElements("th")

                for (j in 0..6) {
                    date[j] = (th[j + 1] as Element).content.toString()
                }

                break
            }
        }

        return date
    }

    /*
      getKcalNew
     */
  /*  internal fun getKcalNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                            schulKndScCode: String, schMmealScCode: String): Array<String?> {
        val content = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode)

        return getKcalSubNew(content, url)
    }*/

    internal fun getKcalNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                            schulKndScCode: String, schMmealScCode: String, year: String, month: String, day: String): Array<String?> {
        val content = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day)

        return getKcalSubNew(content, url)
    }

    private fun getKcalSubNew(content: Array<String?>, url: String): Array<String?> {
        var mSource: Source? = null

        try {
            val mUrl = URL(url)

            var mStream: InputStream? = null

            try {
                val urlConnection = mUrl.openConnection() as HttpsURLConnection
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.hostnameVerifier = hostnameVerifier
                mStream = urlConnection.inputStream
                mSource = Source(mStream!!)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (mStream != null) {
                    mStream.close()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        assert(mSource != null)
        mSource!!.fullSequentialParse()
        val table = mSource.getAllElements("table")

        work@ for (i in table.indices) {
            if ((table[i] as Element).getAttributeValue("class") == "tbl_type3") {
                val tbody = (table[i] as Element).getAllElements("tbody")
                val tr = (tbody[0] as Element).getAllElements("tr")

                //
                // Version 8 Update
                //
                // 칼로리 정보를 담고있는 tr태그의 index가 43-44로 변하는 현상을 발견했습니다.
                // 이로인해 칼로리를 가져오지 못하는 오류가 발생했었습니다.
                // 그래서 넉넉하게 42부터 45까지 칼로리 정보의 index를 검사하도록 반복문을 구성했습니다.

                for (index in 42..45) {
                    val th = (tr[index] as Element).getAllElements("th")

                    if ((th[0] as Element).content.toString() == "에너지(kcal)") {
                        val td = (tr[index] as Element).getAllElements("td")

                        for (j in td.indices) {
                            content[j] = (td[j] as Element).content.toString()
                        }

                        break@work
                    }
                }

                for (index in content.indices) {
                    content[index] = null
                }

                break
            }
        }

        return content
    }

    /*
      getMealNew
     */
    /*fun getMealNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                   schulKndScCode: String, schMmealScCode: String): Array<String?> {

        val content = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode)

        return getMealNewSub(content, url)
    }*/

    internal fun getMealNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                            schulKndScCode: String, schMmealScCode: String, year: String, month: String, day: String): Array<String?> {

        val content = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day)

        return getMealNewSub(content, url)
    }

    private fun getMealNewSub(content: Array<String?>, url: String): Array<String?> {
        var mSource: Source? = null

        try {
            val mUrl = URL(url)

            var mStream: InputStream? = null

            try {
                val urlConnection = mUrl.openConnection() as HttpsURLConnection
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.hostnameVerifier = hostnameVerifier
                mStream = urlConnection.inputStream
                mSource = Source(mStream!!)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (mStream != null) {
                    mStream.close()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        assert(mSource != null)
        mSource!!.fullSequentialParse()
        val table = mSource.getAllElements("table")

        for (i in table.indices) if ((table[i] as Element).getAttributeValue("class") == "tbl_type3") {
            val tBody = (table[i] as Element).getAllElements("tbody")
            val tr = (tBody[0] as Element).getAllElements("tr")
            val title = (tr[2] as Element).getAllElements("th")

            if ((title[0] as Element).content.toString() == "식재료") {
                val tdMeal = (tr[1] as Element).getAllElements("td")

                (0..6).forEach {
                    content[it] = (tdMeal[it] as Element).content.toString()
                    content[it] = content[it]!!.replace("<br />", "\n")
                }

                break
            }

            for (index in content.indices) {
                content[index] = null
            }

            break
        }

        return content
    }

    /*
      getPeopleNew
     */
   /* fun getPeopleNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                     schulKndScCode: String, schMmealScCode: String): Array<String?> {
        val content = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode)

        return getPeopleSubNew(content, url)
    }

    fun getPeopleNew(CountryCode: String, schulCode: String, schulCrseScCode: String,
                     schulKndScCode: String, schMmealScCode: String, year: String, month: String, day: String): Array<String?> {
        val content = arrayOfNulls<String>(7)
        val url = ("https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day)

        return getPeopleSubNew(content, url)
    }

    private fun getPeopleSubNew(content: Array<String?>, url: String): Array<String?> {
        var mSource: Source? = null

        try {
            val mUrl = URL(url)

            var mStream: InputStream? = null

            try {
                val urlConnection = mUrl.openConnection() as HttpsURLConnection
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.hostnameVerifier = hostnameVerifier
                mStream = urlConnection.inputStream
                mSource = Source(mStream!!)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (mStream != null) {
                    mStream.close()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        assert(mSource != null)
        mSource!!.fullSequentialParse()
        val table = mSource.getAllElements("table")

        for (i in table.indices) {
            if ((table[i] as Element).getAttributeValue("class") == "tbl_type3") {
                val tbody = (table[i] as Element).getAllElements("tbody")
                val tr = (tbody[0] as Element).getAllElements("tr")
                val th = (tr[0] as Element).getAllElements("th")

                if ((th[0] as Element).content.toString() == "급식인원") {
                    val td = (tr[0] as Element).getAllElements("td")

                    for (j in 0..6) {
                        content[j] = (td[j] as Element).content.toString()
                    }

                    break
                }

                for (index in content.indices) {
                    content[index] = null
                }

                break
            }
        }

        return content
    }
*/
    /**
     * isMealCheck meal이 "", " ", null이면 false, 아니면 true
     */
    internal fun isMealCheck(meal: String?): Boolean {
        return !("" == meal || " " == meal || meal == null)
    }
}