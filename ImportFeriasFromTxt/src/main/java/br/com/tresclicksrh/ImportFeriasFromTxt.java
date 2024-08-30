package br.com.tresclicksrh;

import br.com.tresclicksrh.bencorp_integrations.dao.ColaboradorDAO;
import br.com.tresclicksrh.bencorp_integrations.dto.ColaboradorDto;
import br.com.tresclicksrh.bencorp_integrations.utils.TratamentoDeData;

import java.io.*;

public class ImportFeriasFromTxt {

        private static final String fileName = "C:/Users/fabio/Downloads/20240829-BencorpFerias.txt";

        public static void main(String[] args) throws IOException {

            try {
                FileReader lerArquivo = new FileReader(ImportFeriasFromTxt.fileName);

                BufferedReader br = new BufferedReader(lerArquivo);

                //String linhaLida = br.readLine(); // readLine lê uma linha de texto completa
                int intLido;
                int contadorTab=0;

                String linha = br.readLine();
                String[] colunas = null;

                ColaboradorDto colaboradorDto = null;

                ColaboradorDAO dao = new ColaboradorDAO();

                while (linha != null) {

                    colaboradorDto = new ColaboradorDto();

                    colunas = linha.split(Character.toString((char) 9));
                    System.out.println("LINHA: "+ colunas.toString());

                    colaboradorDto.setCodigo(colunas[1]);
                    colaboradorDto.setNome(colunas[2]);
                    colaboradorDto.setDataAdmissao(TratamentoDeData.parseDate(colunas[3]));
                    colaboradorDto.setDataLimiteParaGozo(TratamentoDeData.parseDate(colunas[12]));
                    colaboradorDto.setInicioPeriodoAquisitivo(TratamentoDeData.parseDate(colunas[15]));
                    colaboradorDto.setFimPeriodoAquisitivo(TratamentoDeData.parseDate(colunas[16]));
                    colaboradorDto.setPeriodoVencido(Integer.parseInt(colunas[19]));
                    colaboradorDto.setQtdAvosDeFeriasDoPeriodoAquisitivo(colunas[20].equals("0") ? 12 : Integer.parseInt(colunas[20].split(",")[0]));
                    colaboradorDto.setQtdDiasGozados(Integer.parseInt(colunas[23].split(",")[0]));
                    colaboradorDto.setQtdDiasRestantes(Integer.parseInt(colunas[35].split(",")[0]));
                    colaboradorDto.setQtdFaltasNoPeriodo(Integer.parseInt(colunas[46].split(",")[0]));

                    colaboradorDto.setAbonoPecuniario(false);
                    colaboradorDto.setAdiantamento13Salario(false);

                    dao.save(colaboradorDto);

                    linha = br.readLine();
                }

                dao.close(); //fecha conexão com banco de dados
                br.close(); // fechando o buffer

                lerArquivo.close(); // fechando a leitura do arquivo



            } catch (Exception e) {
                e.printStackTrace();

            }
        }
}


                /*
                 FileInputStream arquivo = new FileInputStream(new File(ImportFeriasFromTxt.fileName));

                HSSFWorkbook workbook = new HSSFWorkbook(arquivo);
                HSSFSheet sheetColaboradors = workbook.getSheetAt(0);

                XSSFWorkbook workbook = new XSSFWorkbook(arquivo);
                XSSFSheet sheetColaboradors = workbook.getSheetAt(0);


                Iterator<Row> rowIterator = sheetColaboradors.iterator();

                while (rowIterator.hasNext(): {

                    Row row = rowIterator.next();
                    //pula o cabeçalho e linhas em branco
                    if (row.getRowNum() >= 8: {
                        Iterator<Cell> cellIterator = row.cellIterator();

                        ColaboradorDto colaboradorDto = new ColaboradorDto();
                        colaboradores.add(colaboradorDto);

                        while (cellIterator.hasNext(): {
                            Cell cell = cellIterator.next();
                            switch (cell.getColumnIndex(): {
                                case 0:
                                    colaboradorDto.setCodigo(cell.getStringCellValue());
                                    break;
                                case 3:
                                    colaboradorDto.setNome(cell.getStringCellValue());
                                    break;

                            }
                        }
                    }

                 */