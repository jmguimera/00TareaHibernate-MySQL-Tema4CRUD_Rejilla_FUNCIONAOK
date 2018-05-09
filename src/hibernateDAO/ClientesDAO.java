/*
 * ClientesDAO.java
 */

package hibernateDAO;

import clientes.entity.Clientes;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClientesDAO
{  
    private Session sesion; 
    private Transaction tx;  

    // insertando un registro
    public void guardaCliente(Clientes cliente) throws HibernateException 
    { 

      try 
        { 
            iniciaOperacion(); 
            // operaicon save de hibernate pasando el objeto
            sesion.save(cliente);
            // confirma la la grabacion
            tx.commit(); 
        }
      catch (HibernateException he) 
        { 
            // envia el error he si lo hay a la rutina que maneja los mensajes
            manejaExcepcion(he); 
            throw he; 
        }
      finally 
        { 
            // en cualquier caso cierra la sesion
            sesion.close(); 
        }  

    }  

    // modificando un registro (actualizarlo)
    public void actualizaCliente(Clientes cliente) throws HibernateException 
    { 
        try 
        { 
            iniciaOperacion(); 
            // actualiozacion de hibernate
            sesion.update(cliente); 
            // confirma la grabacion
            tx.commit(); 
        }
        catch (HibernateException he) 
        { 
            manejaExcepcion(he); 
            throw he; 
        }
        finally 
        { 
            sesion.close(); 
        } 
    }  

    // borrando un registro de la tabla
    public void eliminaCliente(Clientes cliente) throws HibernateException 
    { 
        try 
        { 
            iniciaOperacion(); 
            sesion.delete(cliente); 
            tx.commit(); 
        }
        catch (HibernateException he) 
        { 
            manejaExcepcion(he); 
            throw he; 
        }
        finally 
        { 
            sesion.close(); 
        } 
    }  

    // buscando un registro
    public Clientes getCliente(String clienteNif) throws HibernateException 
    { 
        Clientes cliente = null;  
        try 
        { 
            iniciaOperacion(); 
            // localiza el objeto mediante el atributo
            cliente = (Clientes) sesion.get(Clientes.class, clienteNif); 
        }
        finally 
        { 
            sesion.close(); 
        }  

        return cliente; 
    }  

    // se obtiene una List coleccion con los objetos clientes
    // lo use para comprobar que la parte de hibernate funcionaba correctamente
    public List<Clientes> getListaClientes() throws HibernateException 
    { 
        List<Clientes> listaClientes = null;  

        try 
        { 
            iniciaOperacion(); 
            listaClientes = sesion.createQuery("select c.nif,c.nombre from Clientes c").list(); 
        } finally 
        { 
            sesion.close(); 
        }  

        return listaClientes; 
    }  
     // aqui llenamos el modelo de la rejilla    
    public DefaultTableModel llenaRejilla(DefaultTableModel dtm){
        // rutina de creacion de sesion y transaction
        iniciaOperacion();
        
        // traemos todos los objetos clientes de la table
        Query q =sesion.createQuery("from Clientes");
        
        // creamos objeto lista de una coleccion list tipo Clientes retornado
        // por el metodo list() del objeto q de la clase Query
        List<Clientes> lista =q.list();
        
        // creamos el iterador iter de la lista
        Iterator<Clientes> iter=lista.iterator();
        
        // cerramos la transaction
        sesion.close();

        while(iter.hasNext())
        {
         // aqui obtenemos cada objeto cliente que hay en la lista
          Clientes cliente=iter.next();
          
          // luego creamos el objeto fila con los datos que mostrara la tabla,
          // de los gets de las propiedades que necesitamos nif y nombre
          // que eslo que mostraŕa nuestra rejilla
          Object[] fila={cliente.getNif(),cliente.getNombre()};
          
          // añadimos la fila a la rejilla
            dtm.addRow(fila);
        }
        //aqui devolvemos el modelo con el contenido de la rejilla
        return dtm;
    }

    // rutina de inicio de operacion de cada transacción sobre la base de datos
    // crea el objeto sesion e inicia la transaction 
    private void iniciaOperacion() throws HibernateException 
    { 
        // se crea el objeto sesion y la abre
        sesion = HibernateUtil.getSessionFactory().openSession(); 
        // se crea un objeto transaction abierta
        tx = sesion.beginTransaction(); 
    }  
    // rutina de captura de errores de Exception que lanze hibernate
    private void manejaExcepcion(HibernateException he) throws HibernateException 
    { 
        // cualquier error que suceda devuelve todo a su estado anterior de la
        // transaction en curso en la sesion
        tx.rollback(); 
        throw new HibernateException("Ocurrió un error de acceso a datos en ClienteDAO", he); 
    } 
}
