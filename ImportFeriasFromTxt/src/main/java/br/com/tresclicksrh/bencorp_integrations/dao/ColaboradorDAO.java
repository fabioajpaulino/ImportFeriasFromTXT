package br.com.tresclicksrh.bencorp_integrations.dao;

import br.com.tresclicksrh.bencorp_integrations.dto.ColaboradorVacationDto;
import br.com.tresclicksrh.bencorp_integrations.utils.DbConnect;
import br.com.tresclicksrh.bencorp_integrations.utils.TratamentoDeData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColaboradorDAO {

    private final static Logger logger = LoggerFactory.getLogger("br.com.tresclicksrh.bencorp_integrations");

    private Connection conn;;

    public ColaboradorDAO() {
        DbConnect dbConn = new DbConnect();
        conn = dbConn.getConn(conn);
    }

    public void close() {
        try {
            if (conn!=null) conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Integer intIgnorarDiasPendenteNaIntegracao, Integer p_company_id ) {
        String deleteSQL= "delete from vacations v " +
                "where company_id=" + p_company_id +
                " and v.updated_at < (current_date - integer '"+ intIgnorarDiasPendenteNaIntegracao +"')";
        Statement st = null;
        int retorno=0;

        try {
            st = conn.createStatement();
            //retorno = st.executeUpdate(deleteSQL);
            st.close();

        } catch (Exception ex) {

            logger.error(ex.getMessage());
            logger.error("ERRO:" + deleteSQL);
        }
        return retorno;
    }

    public int saveUpdate (ColaboradorVacationDto colaboradorVacationDto, Integer intIgnorarDiasPendenteNaIntegracao) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sqlUpdate = null;
        int retorno=0;
        Statement st = null;
        ResultSet resultado = null;
        Integer userId = null;

        String selectUserSQL = "SELECT u.id AS user_id FROM users u " +
                "WHERE UPPER(u.name) = UPPER('" + colaboradorVacationDto.getNome().toUpperCase()+ "')";

        try {
            st = conn.createStatement();
            resultado = st.executeQuery(selectUserSQL);

            while (resultado.next()) {
                userId = resultado.getInt("user_id");
                colaboradorVacationDto.setTargetUserId(userId);
            }

            sqlUpdate = "UPDATE vacations " +
                    " SET days_available= " + colaboradorVacationDto.getQtdDiasRestantes() + "," +
                    " days_used= " + colaboradorVacationDto.getQtdDiasGozados() +
                    " WHERE target_user_id = " + colaboradorVacationDto.getTargetUserId() +
                    " AND updated_at < (current_date - interval '"+intIgnorarDiasPendenteNaIntegracao+" days') " +
                    " AND acquisition_period_start = '" + colaboradorVacationDto.getDataAdmissao().format(dtf) + "' ";

            int qtdLinhasAfetadas = st.executeUpdate(sqlUpdate);

            if (qtdLinhasAfetadas==0) {

                /*String sqlBuscaVacation = "SELECT id FROM vacations " +
                                        " WHERE target_user_id = " + colaboradorVacationDto.getTargetUserId() +
                                        " AND acquisition_period_start = '" + colaboradorVacationDto.getInicioPeriodoAquisitivo() + "'";

                 */
                String sqlBuscaVacation = "SELECT vi.id FROM vacations v, vacation_items vi"+
                                            " WHERE v.id = vi.vacation_id AND target_user_id = " + colaboradorVacationDto.getTargetUserId() +
                                            " AND vi.updated_at < (current_date - integer '" + (intIgnorarDiasPendenteNaIntegracao) + "') ";

                logger.info(sqlBuscaVacation);
                resultado = st.executeQuery(sqlBuscaVacation);
                if (resultado.next()) {
                    logger.info("Colaborador com férias cadastradas dentro de " + intIgnorarDiasPendenteNaIntegracao + " dias.");
                } else {

                    String status = colaboradorVacationDto.isPeriodoVencido() ? "open" : "";

                    String insertVacation = "INSERT INTO vacations (id, uuid, acquisition_period_start, acquisition_period_end, concessive_period_start, " +
                            "concessive_period_end, days_available, days_used, created_at, updated_at, target_user_id, " + //solicitation_id, approved_by_manager_id, approved_by_rh_id, " +
                            "created_by_id,  company_id,  status) " +
                            "VALUES (nextval('vacations_id_seq'), gen_random_uuid(), '" +
                            colaboradorVacationDto.getInicioPeriodoAquisitivo() + "','" +
                            colaboradorVacationDto.getFimPeriodoAquisitivo() + "','" +
                            TratamentoDeData.somaDias(colaboradorVacationDto.getFimPeriodoAquisitivo(), 1) + "','" +
                            colaboradorVacationDto.getDataLimiteParaGozo() + "'," +
                            colaboradorVacationDto.getQtdDiasRestantes() + "," +
                            colaboradorVacationDto.getQtdDiasGozados() + "," +
                            "(current_date - integer '" + (intIgnorarDiasPendenteNaIntegracao + 1) + "'), " +
                            "(current_date - integer '" + (intIgnorarDiasPendenteNaIntegracao + 1) + "')," +
                            colaboradorVacationDto.getTargetUserId() + "," +
                            colaboradorVacationDto.getCreated_by_id() + "," + colaboradorVacationDto.getCompany_id() + ",'" +
                            status + "')";

                    st.executeUpdate(insertVacation);
                    logger.info("INSERT OK->" + insertVacation);
                }
            } else {
                logger.info("UPDATE OK->" + sqlUpdate);
            }

            resultado.close();
            st.close();
            retorno++;

        } catch (Exception ex) {

            logger.error("ERRO-> " + ex.getMessage());
            logger.error("ERRO-> " + sqlUpdate);
        }

        return retorno;
    }

    public int save(ColaboradorVacationDto colaboradorVacationDto, Integer intIgnorarDiasPendenteNaIntegracao) {

        Statement st = null;
        ResultSet resultado = null;

        // verifica se o colaborador atual possui férias cadastradas pendentes de integração (definido pelo prazo na variável intIgnorarDiasPendenteNaIntegracao)
        String selectUserSQL = "SELECT u.id AS user_id " +
                "FROM users u " +
                "RIGHT JOIN vacations v ON u.id = v.target_user_id " +
                "AND v.updated_at < (current_date - interval '"+intIgnorarDiasPendenteNaIntegracao+" days') " +
                "WHERE UPPER(u.name) = UPPER('" + colaboradorVacationDto.getNome().toUpperCase()+ "') GROUP BY u.id";

        //String selectUserSQL = "SELECT id FROM users where upper(name) = '" + colaboradorDto.getNome().toUpperCase()+ "'";

        String insertVacation = null;
        int retorno=0;

        try {
            st = conn.createStatement();

            //verifica se o colaborador atual possui períodos de férias, ou seja, se já está na vacations
            String qtdRegistrosSQL = "SELECT count(u.id) AS userFeriasCadastrada " +
                                    "FROM users u JOIN vacations v ON u.id = v.target_user_id " +
                                    "WHERE UPPER(u.name) = UPPER('" + colaboradorVacationDto.getNome().toUpperCase()+ "')";
            resultado = st.executeQuery(qtdRegistrosSQL);
            resultado.next();
            Integer id = null;

            if (resultado.getInt("userFeriasCadastrada")==0) {

                logger.error("Não existia vacations cadastrada: " + qtdRegistrosSQL);

                selectUserSQL = "SELECT u.id AS user_id FROM users u " +
                                "WHERE UPPER(u.name) = UPPER('" + colaboradorVacationDto.getNome().toUpperCase()+ "')";
            }

            resultado = st.executeQuery(selectUserSQL);

            while (resultado.next()) {
                id = resultado.getInt("user_id");
                colaboradorVacationDto.setTargetUserId(id);
            }

            if (id==null) {
                logger.error("Não encontrado ou férias atualizada aguardando integração com a folha nos últimos " + intIgnorarDiasPendenteNaIntegracao + " dias: " + selectUserSQL);
            } else {

                String status = colaboradorVacationDto.isPeriodoVencido()?"open":"";

                insertVacation = "INSERT INTO vacations (id, uuid, acquisition_period_start, acquisition_period_end, concessive_period_start, " +
                        "concessive_period_end, days_available, days_used, created_at, updated_at, target_user_id, " + //solicitation_id, approved_by_manager_id, approved_by_rh_id, " +
                        "created_by_id,  company_id,  status) " +
                        "VALUES (nextval('vacations_id_seq'), gen_random_uuid(), '" +
                        colaboradorVacationDto.getInicioPeriodoAquisitivo() + "','" +
                        colaboradorVacationDto.getFimPeriodoAquisitivo() + "','" +
                        TratamentoDeData.somaDias(colaboradorVacationDto.getFimPeriodoAquisitivo(), 1) + "','" +
                        colaboradorVacationDto.getDataLimiteParaGozo() + "'," +
                        colaboradorVacationDto.getQtdDiasRestantes() + "," +
                        colaboradorVacationDto.getQtdDiasGozados() + "," +
                        "(current_date - integer '"+(intIgnorarDiasPendenteNaIntegracao+1)+"'), " +
                        "(current_date - integer '"+(intIgnorarDiasPendenteNaIntegracao+1)+"')," +
                        colaboradorVacationDto.getTargetUserId() + "," +
                        colaboradorVacationDto.getCreated_by_id() + "," + colaboradorVacationDto.getCompany_id() + ",'" +
                        status + "')";

                st.executeUpdate(insertVacation);
                retorno++;

            }

            resultado.close();
            st.close();

        } catch (Exception ex) {

            logger.error(ex.getMessage());
            logger.error(insertVacation);
        }
        return retorno;
    }
}
