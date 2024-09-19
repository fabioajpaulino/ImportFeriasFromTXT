package br.com.tresclicksrh.bencorp_integrations.dao;

import br.com.tresclicksrh.bencorp_integrations.dto.ColaboradorDto;
import br.com.tresclicksrh.bencorp_integrations.utils.DbConnect;
import br.com.tresclicksrh.bencorp_integrations.utils.TratamentoDeData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public int delete(Integer intIgnorarDiasPendenteNaIntegracao) {
        String deleteSQL= "delete vacations v where v.updated_at < (current_date - integer '"+ intIgnorarDiasPendenteNaIntegracao +"')";
        Statement st = null;
        int retorno=0;

        try {
            st = conn.createStatement();
            retorno = st.executeUpdate(deleteSQL);
            st.close();

        } catch (Exception ex) {

            logger.error(ex.getMessage());
            logger.error(deleteSQL);
        }
        return retorno;
    }

    public int save(ColaboradorDto colaboradorDto, Integer intIgnorarDiasPendenteNaIntegracao) {

        Statement st = null;
        ResultSet resultado = null;


        String selectUserSQL = "SELECT u.id AS user_id " +
                "FROM users u " +
                "RIGHT JOIN vacations v ON u.id = v.target_user_id " +
                "AND v.updated_at < (current_date - interval '"+intIgnorarDiasPendenteNaIntegracao+" days') " +
                "WHERE UPPER(u.name) = UPPER('" + colaboradorDto.getNome().toUpperCase()+ "') GROUP BY u.id";

        //String selectUserSQL = "SELECT id FROM users where upper(name) = '" + colaboradorDto.getNome().toUpperCase()+ "'";

        String insertVacation = null;
        int retorno=0;

        try {
            st = conn.createStatement();

            resultado = st.executeQuery(selectUserSQL);
            Integer id = null;
            while (resultado.next()) {
                id = resultado.getInt("user_id");
                colaboradorDto.setId(id);
            }

            logger.error(id==null?"Não encontrado ou possui solicitação de férias cadastrada nos últimos " + intIgnorarDiasPendenteNaIntegracao + " dias: " + selectUserSQL : "OK - inserindo período aquisitivo.");

            int created_by_id = 1; //para identificar que foi através da integração usar sempre 1 rh@3clicksrh.com.br
            int company_id = 2; //2= bencorp ou 1=Via
            String status = colaboradorDto.isPeriodoVencido()?"open":"";

            if (id!=null) {
                insertVacation = "INSERT INTO vacations (id, uuid, acquisition_period_start, acquisition_period_end, concessive_period_start, " +
                        "concessive_period_end, days_available, days_used, created_at, updated_at, target_user_id, " + //solicitation_id, approved_by_manager_id, approved_by_rh_id, " +
                        "created_by_id,  company_id,  status) " +
                        "VALUES (nextval('vacations_id_seq'), gen_random_uuid(), '" +
                        colaboradorDto.getInicioPeriodoAquisitivo() + "','" +
                        colaboradorDto.getFimPeriodoAquisitivo() + "','" +
                        TratamentoDeData.somaDias(colaboradorDto.getFimPeriodoAquisitivo(), 1) + "','" +
                        colaboradorDto.getDataLimiteParaGozo() + "'," +
                        colaboradorDto.getQtdDiasRestantes() + "," +
                        colaboradorDto.getQtdDiasGozados() + "," +
                        "(current_date - integer '"+(intIgnorarDiasPendenteNaIntegracao+1)+"'), " +
                        "(current_date - integer '"+(intIgnorarDiasPendenteNaIntegracao+1)+"')," +
                        colaboradorDto.getId() + "," +
                        created_by_id + "," + company_id + ",'" +
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
