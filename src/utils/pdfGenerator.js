import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

export const generatePDF = async (invoiceData) => {
  try {
    // 인보이스 문서 요소 선택
    const element = document.getElementById('invoice-document');
    
    if (!element) {
      alert('인보이스 문서를 찾을 수 없습니다.');
      return;
    }

    // HTML을 캔버스로 변환
    const canvas = await html2canvas(element, {
      scale: 2, // 고해상도를 위해 스케일 증가
      useCORS: true,
      allowTaint: true,
      backgroundColor: '#ffffff'
    });

    // 캔버스를 이미지로 변환
    const imgData = canvas.toDataURL('image/png');
    
    // PDF 생성
    const pdf = new jsPDF('p', 'mm', 'a4');
    const imgWidth = 210; // A4 너비 (mm)
    const pageHeight = 295; // A4 높이 (mm)
    const imgHeight = (canvas.height * imgWidth) / canvas.width;
    let heightLeft = imgHeight;

    let position = 0;

    // 첫 페이지 추가
    pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
    heightLeft -= pageHeight;

    // 추가 페이지가 필요한 경우
    while (heightLeft >= 0) {
      position = heightLeft - imgHeight;
      pdf.addPage();
      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;
    }

    // 파일명 생성
    const invoiceNumber = invoiceData.invoiceDetails.number || 'INV-001';
    const fileName = `Invoice_${invoiceNumber}_${invoiceData.invoiceDetails.date}.pdf`;

    // PDF 다운로드
    pdf.save(fileName);
    
  } catch (error) {
    console.error('PDF 생성 중 오류가 발생했습니다:', error);
    alert('PDF 생성 중 오류가 발생했습니다. 다시 시도해주세요.');
  }
};
