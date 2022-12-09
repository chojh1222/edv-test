package query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MakeHan {
	// 한글 처리
	public static String toKorean( String st ) {
	    if (st == null) return null;
	    try {
	      st = new String( st.getBytes("8859_1"), "euc-kr" );
	    } catch ( java.io.UnsupportedEncodingException e ) {
	      System.out.println( e.toString() );
	    }
	    return st;
	  }
	
	// 게시물 25자 이상 자르기
	public static String sub(String Title) throws Exception{
		if(Title.length()<25){
			return Title;
		}else{
			Title.substring(0,25);
		}
		return Title;
		}

	//	문자가 null 인지를 체크 하고 숫자형 변환
  	public static int stringChk(String value, String value1) {
    	try {
			if(value == null){	value = "1";	}			
		}catch(Exception e){	value = value1;	}	
    	return Integer.parseInt(value);
  	}
  
	
	//pageing
	public static String navition(int cnt, int pageno){
		StringBuffer buf=new StringBuffer();
		
		int rsCon;
		int iPageNo; //현재 page 번호
		int pagePerBlock=10; //page 게시물 건수
		int nowBlock;
		int totalPage; //총 page 건수
		int istart=0; //page당 게시물 시작
		int iend=0;   //page당 게시물 끝
		int idx;

		if(cnt<=0){
			cnt=0;
			rsCon=0;
		}else{
			rsCon=1;
		}

		if (pageno==0){
			iPageNo=1;
		}else{
			iPageNo=pageno;
		}
		
		nowBlock=(iPageNo-1)/pagePerBlock;
		
		if(rsCon==1){
			totalPage=cnt/pagePerBlock;
			istart=(iPageNo-1)*pagePerBlock;
			iend=iPageNo*pagePerBlock;
			
			if((cnt % pagePerBlock)!=0){
				totalPage=totalPage+1;
				
				if(iPageNo==totalPage){
					iend=cnt;
				}
			}
		}else{
			totalPage=1;
			istart=0;
			iend=0;
		}
	
	
		buf.append("<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">");
		buf.append("<tr>");   
		buf.append("<td width=\"40\"><img src=\"../../images/prev.gif\" width=\"12\" height=\"12\" hspace=\"5\" border=0");     
		if (iPageNo>1){
			buf.append("  onclick=\"javascript:Go('1');\" style='cursor:hand' ");
			}
		buf.append("><img src=\"../../images/p10.gif\" width=\"12\" height=\"12\" border=0 ");
		if (nowBlock>0){
			buf.append(" onclick=\"javascript:Go('"+Integer.toString(nowBlock*pagePerBlock)+"');\" style='cursor:hand' ");
			}
		buf.append("></td>");
		
		
		buf.append(" <td align=center> ");
		int p;
		for( idx=1; idx<=pagePerBlock; idx++){
				p=nowBlock * pagePerBlock + idx;
				if (p==iPageNo){
					buf.append(" &nbsp;<strong>"+p+"</strong> ");
					}else{
						buf.append(" &nbsp;<a href=\"javascript:Go('"+p+"');\" target=_self>"+p+"</a> ");
						}
			if (p==totalPage)
			break;
			}
		buf.append(" &nbsp;</td> ");
		buf.append(" <td width=\"40\" align=\"right\"><img src=\"../../images/n10.gif\" width=\"12\" height=\"12\" border=0 ");
	    if ((nowBlock+1)*pagePerBlock < totalPage){
	    	buf.append(" onclick=\"javascript:Go('"+Integer.toString(((nowBlock+1)*pagePerBlock+1))+");\" style='cursor:hand' ");
	    	}
	    buf.append(" ><img src=\"../../images/next.gif\" width=\"12\" height=\"12\" hspace=\"5\" border=0 ");
	    if (iPageNo < totalPage){
	    	buf.append(" onclick=\"javascript:Go('"+totalPage+"');\" style='cursor:hand'");
	    	}
		buf.append(" ></td> ");
		buf.append(" </tr> ");  
		buf.append("</table>");
		return buf.toString();
	
	}
	public String getDate (){
		Date now = new Date ( );
		SimpleDateFormat sdf4 = new SimpleDateFormat ( "yyyy/MM/dd HH:mm EE" );
		sdf4.setTimeZone ( TimeZone.getTimeZone ( "Asia/Seoul" ) );
		return sdf4.format ( now );
	}

	}


