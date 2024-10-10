package br.com.tresclicksrh;

import br.com.tresclicksrh.bencorp_integrations.dao.ColaboradorDAO;
import br.com.tresclicksrh.bencorp_integrations.dto.ColaboradorVacationDto;
import br.com.tresclicksrh.bencorp_integrations.utils.TratamentoDeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportFeriasFromTxt {

        private static final String p_dirName = "C:\\integracoes\\bencorp";
        private static final String p_fileName = "ferias.txt";
        private static final Integer intIgnorarDiasPendenteNaIntegracao = 3;
        private static final Integer intQtdMinutos = 5;

        private final static Logger logger1 = LoggerFactory.getLogger("br.com.tresclicksrh.bencorp_integrations");

        private final static int created_by_id = 1; //para identificar que foi através da integração usar sempre 1 rh@3clicksrh.com.br
        private final static int company_id = 2; //2= bencorp ou 1=Via

    public static void main(String[] args) throws IOException {
            Timer timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                @Override
                public void run() {
                    try {
                        integracaoFerias(args);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            // Agendar a tarefa para rodar a cada 10 minutos (600000 ms)
            timer.scheduleAtFixedRate(tarefa, 0, intQtdMinutos * 60 * 1000);
        }

        public static void integracaoFerias(String[] args) throws IOException {
            int contadorErro = 0;
            int contadorOk = 0;

            String dirName = args!=null && args.length>0 && args[0]!=null ? args[0] : p_dirName;
            String fileName = args!=null && args.length>0  && args[1]!=null ? args[1] : p_fileName;

            String ambiente = args!=null && args.length>0 && args[2]!=null ? args[2] : "DEV";

            try {
                FileReader lerArquivo = new FileReader(dirName + "\\" + fileName);

                BufferedReader br = new BufferedReader(lerArquivo);

                String linha = br.readLine();
                String[] colunas = null;

                ColaboradorVacationDto colaboradorVacationDto = null;

                ColaboradorDAO dao = new ColaboradorDAO(ambiente);

                while (linha != null) {

                    //if (contadorErro==0 && contadorOk==0) dao.delete(intIgnorarDiasPendenteNaIntegracao, company_id);

                    colaboradorVacationDto = new ColaboradorVacationDto();

                    colunas = linha.split(Character.toString((char) 9));
                    //System.out.println("LINHA: " + colunas.toString());

                    colaboradorVacationDto.setCodigo(colunas[1]);
                    colaboradorVacationDto.setNome(colunas[2]);
                    colaboradorVacationDto.setDataAdmissao(TratamentoDeData.parseDate(colunas[3]));
                    colaboradorVacationDto.setDataLimiteParaGozo(TratamentoDeData.parseDate(colunas[12]));
                    colaboradorVacationDto.setInicioPeriodoAquisitivo(TratamentoDeData.parseDate(colunas[15]));
                    colaboradorVacationDto.setFimPeriodoAquisitivo(TratamentoDeData.parseDate(colunas[16]));
                    colaboradorVacationDto.setPeriodoVencido(Integer.parseInt(colunas[19]));
                    colaboradorVacationDto.setQtdAvosDeFeriasDoPeriodoAquisitivo(colunas[20].equals("0") ? 12 : Integer.parseInt(colunas[20].split(",")[0]));
                    colaboradorVacationDto.setQtdDiasGozados(Integer.parseInt(colunas[23].split(",")[0]));
                    colaboradorVacationDto.setQtdDiasRestantes(Integer.parseInt(colunas[35].split(",")[0]));
                    colaboradorVacationDto.setQtdFaltasNoPeriodo(Integer.parseInt(colunas[46].split(",")[0]));

                    colaboradorVacationDto.setAbonoPecuniario(false);
                    colaboradorVacationDto.setAdiantamento13Salario(false);

                    colaboradorVacationDto.setCompany_id(company_id);
                    colaboradorVacationDto.setCreated_by_id(created_by_id);

                    if (dao.saveUpdate(colaboradorVacationDto,intIgnorarDiasPendenteNaIntegracao)==1) {
                        contadorOk++;
                    } else {
                        contadorErro++;
                    }

                    linha = br.readLine();
                }

                dao.close(); //fecha conexão com banco de dados
                br.close(); // fechando o buffer

                lerArquivo.close(); // fechando a leitura do arquivo

                renomeiaArquivo(dirName, fileName);

            } catch (Exception e) {
                logger1.error(e.getMessage());
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            logger1.error("Execução: " + dtf.format(now));
            logger1.error("Erros:" + Integer.toString(contadorErro));
            logger1.error("OK:" + Integer.toString(contadorOk));

        }

    private static void renomeiaArquivo(String diretorio, String nomeArquivo) {
        File pasta = new File(diretorio);
        File arquivo = new File(pasta, nomeArquivo);

        if (arquivo.exists()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dataAtual = sdf.format(new Date());
            File novoArquivo = new File(pasta, dataAtual + "-ferias.txt");

            boolean sucesso = arquivo.renameTo(novoArquivo);
            if (sucesso) {
                logger1.error("Arquivo renomeado para: " + novoArquivo.getAbsolutePath());
            } else {
                logger1.error("Falha ao renomear o arquivo.");
            }
        } else {
            logger1.error("O arquivo 'ferias.txt' não existe.");
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