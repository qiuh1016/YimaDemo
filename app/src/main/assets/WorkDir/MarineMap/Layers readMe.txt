MAP NAME: Globle Map   //ͼ������
MAP ORGINAL SCALE: 10000 //ͼ��ԭʼ������
BEGIN
<
LAYER NAME: NAVTEX��̨  // ͼ������
ATTRIBUTES COUNT: 2     //һ��ͼ������ж�������ֶ�, �������Ҫָ�����Ե���Ŀ
 <
  ATTR NAME: NAME       //��1�������ֶ�����
  DATA TYPE: STRING     //�����ֶ���������, ������:  INT, FLOAT, STRING
 >
 <
  ATTR NAME: ��̨����   //��2�������ֶ�����, ע�������оٵ����Եĸ�����������ָ������Ŀһ��.
  DATA TYPE: INT
 >
GEO TYPE: POINT         // ͼ��ĵ�������, ������:  POINT, LINE, REGION
GEO STYLE COUNT: 1      //һ��ͼ����Զ�� GEO STYLE ��������(һ�������һ������), �������Ҫָ�� GEO STYLE ����Ŀ
 <
 CONDITION:             //ÿ�� GEO STYLE ������һ������,���ĳ���ֶεĲ�ͬȡֵ���ƶ�SYTLE,���������1��ͼ�㲻ͬ���Ͷ����ƶ���ͬSTYLE. ��һ�㶼ֻ��1��style����,Ϊ��.
 MINSCALE:              //��GEO STYLE ����С��ʾ������
 MAXSCALE:              //��GEO STYLE �������ʾ������
 SYMBOL STYLE: 1, RGB(0, 0, 0), 0, 0, 0  // ������ʽΪ "����ID,������ɫ,����ƫ����X,����ƫ����Y,������ת�Ƕ�".
                                         // ���ʹ���Զ������, �����IDΪʹ��SymbolEditor.exe����ɲ鿴���ķ���ID;
                                         // ���ʹ��S52����, ���ʽΪS52������д����ת�Ƕ�, ��"RACNSP01,45",�μ��ļ�"S57Lib\\S57Symbols"
                                         // "����ƫ����X,����ƫ����Y" ָ����������Ŀ����ʾλ�õ�ƫ����, ��λΪ Himeter
                                         // "��ת�Ƕ�"Ϊ������ʾ˳ʱ����ת�Ķ���
 TEXT STYLE: "@0","Arial", 400, NORMAL, RGB(0, 0, 0) //��ʾ�ַ���: "@0"��ʾ��ʾ��0�������ֶε�����; "Arial"��ʾ����; 400�Ǹ߶�(��λ��himeter); NORMAL��BOLDָ���Ƿ�Ϊ����; RGB��������ɫ
 >
>
//////////////////////////////////////////////////////////////////
<
LAYER NAME: INMARSAT���Ǹ��Ƿ�Χ
ATTRIBUTES COUNT: 1
 <
  ATTR NAME: ���ǰ뾶
  DATA TYPE: FLOAT
 >
GEO TYPE: LINE
GEO STYLE COUNT: 1
 <
 CONDITION: 
 MINSCALE: 
 MAXSCALE: 
 BASIC STYLE: SOLID, 50, RGB(37,121,106)  //LINE���͵Ļ������ͷ�������ʽΪ "����,�߿�,��ɫ", ȡֵ����Ϊ: "SOLID(���� DOT ���� DASH), 50(��λHimeter), RGB(37,121,106)"
 CYCLE SYMBOL STYLE: 1,1000                     //������ʽΪ "ѭ������ID,ѭ�����ž�����" 
					        // ���ʹ���Զ������, �����IDΪʹ��SymbolEditor.exe����ɲ鿴���ķ���ID;
                                         	// ���ʹ��S52����, ���ʽΪS52������д����ת�Ƕ�, ��"RACNSP01,45",�μ��ļ�"S57Lib\\S57Symbols"
      						// "ѭ�����ž�����" ָѭ�����ŵľ�����, ��λΪ Himeter
 TEXT STYLE: "@0","Arial", 400, NORMAL, RGB(0, 0, 0) //��LINE�������߶�����ʾ���ֱ�ע,��ʽͬPOINT����
 >
>
//////////////////////////////////////////////////////////////////
<
LAYER NAME: �Զ��徯����
ATTRIBUTES COUNT: 1
 <
  ATTR NAME: ��������
  DATA TYPE: FLOAT
 >
GEO TYPE: REGION
GEO STYLE COUNT: 1
 <
 CONDITION: 
 MINSCALE: 
 MAXSCALE: 
 BASIC STYLE: RGB(255, 0, 0),90  // REGION���͵Ļ������ͷ�������ʽΪ "���ɫ,���͸����", ͸����0~100, 0Ϊȫ͸��,100Ϊȫɫ;
 PATTERN SYMBOL STYLE:           // REGION���͵������ŷ�������ʽΪ "������ID,������ˮƽ������,�����Ŵ�ֱ������": 
					// ���ʹ���Զ������, �����IDΪʹ��SymbolEditor.exe����ɲ鿴���ķ���ID;
                                        // ���ʹ��S52����, ���ʽΪS52������д����ת�Ƕ�, ��"RACNSP01,45",�μ��ļ�"S57Lib\\S57Symbols"
      					// "������ˮƽ������" �� "�����Ŵ�ֱ������"ָ�����ŷֱ���ˮƽ����ʹ�ֱ�����ϵľ�����, ��λΪ Himeter
 BOUNDARY LINE STYLE: SOLID, 50, RGB(37,121,106) //�߽��ߵ�����,��ʽ����ͬLINE���͵�"BASIC STYLE"����;
 TEXT STYLE:"@0","Arial", 400, NORMAL, RGB(0, 0, 0)
 >
>
//////////////////////////////////////////////////////////////////
END
