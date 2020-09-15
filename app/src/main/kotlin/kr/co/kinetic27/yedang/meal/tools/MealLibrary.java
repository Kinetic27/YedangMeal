package kr.co.kinetic27.yedang.meal.tools;

import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/*
 * VERSION 8
 * UPDATE 20180212
 *
 * @author Mir(whdghks913)
 *
 * Use : getDateNew, getKcalNew, getMealNew, getPeopleNew
 * Delete : getDate, getKcal, getMeal, getMonthMeal, getPeople
 */
public class MealLibrary {
    /**
     * Version 8 Update
     *
     * 나이스 서버가 https로 바뀜에 따라 서버 보안서를 검증하는 코드가 필요해졌습니다.
     * 항상 true를 반환하는 verify()를 Override하는 HostnameVerifier를 만들었습니다.
     */
    private static HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // Log.d("hostnameMeal", hostname);
            //String[] hostSplit = hostname.split(".");
            //boolean data = hostSplit[0].equals("stu") && hostSplit[hostSplit.length - 1].equals("kr");

            //Log.d("hostdata", String.valueOf(data) + " / " + hostSplit[hostSplit.length - 1]);
            return hostname.equalsIgnoreCase("stu.goe.go.kr"); // 우리 앱은 어짜피 이것만 ㅇㅇ
        }
    };

    /**
     * getDateNew
     */
    public static String[] getDateNew(String CountryCode, String schulCode, String schulCrseScCode,
                                      String schulKndScCode, String schMmealScCode) {

        String[] date = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode;
        Log.d("meal url: ", url);
        return getDateNewSub(date, url);
    }

    public static String[] getDateNew(String CountryCode, String schulCode, String schulCrseScCode,
                                      String schulKndScCode, String schMmealScCode, String year, String month, String day) {

        String[] date = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;
        Log.d("meal url: ", url);
        return getDateNewSub(date, url);
    }

    private static String[] getDateNewSub(String[] date, String url) {
        Source mSource = null;

        try {
            URL mUrl = new URL(url);

            InputStream mStream = null;


            /**
             * Version 8 Update
             *
             * 나이스의 급식 파싱 url이 https로 바뀜에 따라 보안을 검증하는 추가 코드가 필요해졌습니다.
             * 인증서 정보를 검사하는 코드를 간단하게 추가하였습니다.
             *
             * 코드를 수정함에 따라 Source의 생성자에 들어가야 하는 인수인 URL을 사용하지 못합니다.
             * 저는 jericho parser의 Source.java를 보면 생성자에 InputStream을 사용할 수 있다는 사실을 발견했습니다.
             */
            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection) mUrl.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                mStream = urlConnection.getInputStream();
                mSource = new Source(mStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mStream != null) {
                    mStream.close();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals("tbl_type3")) {
                List<?> tr = ((Element) table.get(i)).getAllElements("tr");
                List<?> th = ((Element) tr.get(0)).getAllElements("th");

                for (int j = 0; j < 7; j++) {
                    date[j] = ((Element) th.get(j + 1)).getContent().toString();
                }

                break;
            }
        }

        return date;
    }

    /**
     * getKcalNew
     */
    public static String[] getKcalNew(String CountryCode, String schulCode, String schulCrseScCode,
                                      String schulKndScCode, String schMmealScCode) {
        String[] content = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode;

        return getKcalSubNew(content, url);
    }

    public static String[] getKcalNew(String CountryCode, String schulCode, String schulCrseScCode,
                                      String schulKndScCode, String schMmealScCode, String year, String month, String day) {
        String[] content = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getKcalSubNew(content, url);
    }

    private static String[] getKcalSubNew(String[] content, String url) {
        Source mSource = null;

        try {
            URL mUrl = new URL(url);

            InputStream mStream = null;

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection) mUrl.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                mStream = urlConnection.getInputStream();
                mSource = new Source(mStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mStream != null) {
                    mStream.close();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        work: for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals("tbl_type3")) {
                List<?> tbody = ((Element) table.get(i)).getAllElements("tbody");
                List<?> __tr = ((Element) tbody.get(0)).getAllElements("tr");

                /**
                 * Version 8 Update
                 *
                 * 칼로리 정보를 담고있는 tr태그의 index가 43-44로 변하는 현상을 발견했습니다.
                 * 이로인해 칼로리를 가져오지 못하는 오류가 발생했었습니다.
                 * 그래서 넉넉하게 42부터 45까지 칼로리 정보의 index를 검사하도록 반복문을 구성했습니다.
                 */
                for (int index = 39; index < 55; index++) {
                    List<?> __th = ((Element) __tr.get(index)).getAllElements("th");
                    Log.d("meals", ((Element) __tr.get(index)).getAllElements("th").toString());
                    if (((Element) __th.get(0)).getContent().toString().equals("에너지(kcal)")) {
                        List<?> td = ((Element) __tr.get(index)).getAllElements("td");
                        Log.d("meal num", index+"");

                        for (int j = 0; j < td.size(); j++) {
                            content[j] = ((Element) td.get(j)).getContent().toString();
                        }

                        break work;
                    }
                }

                for (int index = 0; index < content.length; index++) {
                    content[index] = null;
                }

                break;
            }
        }

        return content;
    }

    /**
     * getMealNew
     */
    public static String[] getMealNew(String CountryCode, String schulCode, String schulCrseScCode,
                                      String schulKndScCode, String schMmealScCode) {

        String[] content = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode;

        return getMealNewSub(content, url);
    }

    public static String[] getMealNew(String CountryCode, String schulCode, String schulCrseScCode,
                                      String schulKndScCode, String schMmealScCode, String year, String month, String day) {

        String[] content = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getMealNewSub(content, url);
    }

    private static String[] getMealNewSub(String[] content, String url) {
        Source mSource = null;

        try {
            URL mUrl = new URL(url);

            InputStream mStream = null;

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection) mUrl.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                mStream = urlConnection.getInputStream();
                mSource = new Source(mStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mStream != null) {
                    mStream.close();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals("tbl_type3")) {
                List<?> tbody = ((Element) table.get(i)).getAllElements("tbody");
                List<?> tr = ((Element) tbody.get(0)).getAllElements("tr");
                List<?> title = ((Element) tr.get(2)).getAllElements("th");

                if (((Element) title.get(0)).getContent().toString().equals("식재료")) {
                    List<?> tdMeal = ((Element) tr.get(1)).getAllElements("td");

                    for (int j = 0; j < 7; j++) {
                        content[j] = ((Element) tdMeal.get(j)).getContent().toString();
                        content[j] = content[j].replace("<br />", "\n");
                    }

                    break;
                }

                for (int index = 0; index < content.length; index++) {
                    content[index] = null;
                }

                break;
            }
        }

        return content;
    }

    /**
     * getPeopleNew
     */
    public static String[] getPeopleNew(String CountryCode, String schulCode, String schulCrseScCode,
                                        String schulKndScCode, String schMmealScCode) {
        String[] content = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode;

        return getPeopleSubNew(content, url);
    }

    public static String[] getPeopleNew(String CountryCode, String schulCode, String schulCrseScCode,
                                        String schulKndScCode, String schMmealScCode, String year, String month, String day) {
        String[] content = new String[7];
        String url = "https://stu." + CountryCode + "/sts_sci_md01_001.do?schulCode=" + schulCode + "&schulCrseScCode="
                + schulCrseScCode + "&schulKndScCode=" + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getPeopleSubNew(content, url);
    }

    private static String[] getPeopleSubNew(String[] content, String url) {
        Source mSource = null;

        try {
            URL mUrl = new URL(url);

            InputStream mStream = null;

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection) mUrl.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                mStream = urlConnection.getInputStream();
                mSource = new Source(mStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mStream != null) {
                    mStream.close();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals("tbl_type3")) {
                List<?> tbody = ((Element) table.get(i)).getAllElements("tbody");
                List<?> __tr = ((Element) tbody.get(0)).getAllElements("tr");
                List<?> __th = ((Element) __tr.get(0)).getAllElements("th");

                if (((Element) __th.get(0)).getContent().toString().equals("급식인원")) {
                    List<?> td = ((Element) __tr.get(0)).getAllElements("td");

                    for (int j = 0; j < 7; j++) {
                        content[j] = ((Element) td.get(j)).getContent().toString();
                    }

                    break;
                }

                for (int index = 0; index < content.length; index++) {
                    content[index] = null;
                }

                break;
            }
        }

        return content;
    }

    /**
     * isMealCheck meal이 "", " ", null이면 false, 아니면 true
     */
    public static boolean isMealCheck(String meal) {
        return !("".equals(meal) || " ".equals(meal) || meal == null);
    }
}