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

    public int save(ColaboradorDto colaboradorDto) {

        Statement st = null;
        ResultSet resultado = null;

        String selectUser = "SELECT id FROM users where lower(name) = '" + colaboradorDto.getNome().toLowerCase()+ "'";
        String insertVacation = null;
        int retorno=0;

        try {
            st = conn.createStatement();

            resultado = st.executeQuery(selectUser);
            Integer id = null;
            while (resultado.next()) {
                id = resultado.getInt("id");
                colaboradorDto.setId(id);
            }

            logger.error(id==null?"Não encontrado: "+selectUser:"");

            int created_by_id = 1; //para identificar que foi via integração usar sempre 1 rh@3clicksrh.com.br
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
                        "CURRENT_TIMESTAMP, CURRENT_TIMESTAMP," +
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
