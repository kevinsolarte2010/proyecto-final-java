package com.mycompany.mavenproject3.api;

import com.mycompany.mavenproject3.dao.ProductoDAO;
import com.mycompany.mavenproject3.model.Producto;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductoApiServlet", urlPatterns = {"/api/productos"})
public class ProductoApiServlet extends HttpServlet {

    // Instanciamos el DAO que es el único que hablará con la base de datos
    private ProductoDAO productoDAO = new ProductoDAO();
    
    /**
     * Método auxiliar para habilitar CORS añadiendo los encabezados necesarios.
     * Permite peticiones de cualquier origen (*) y define los métodos permitidos.
     */
    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    /**
     * El método doOptions es necesario para responder a las peticiones "preflight"
     * que hacen los navegadores web antes de hacer un POST, PUT o DELETE con CORS.
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        setAccessControlHeaders(response); // Habilitar CORS
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            // Le pedimos al DAO que traiga los productos de la BD
            List<Producto> lista = productoDAO.listar();

            StringBuilder respuesta = new StringBuilder();
            respuesta.append("{");
            respuesta.append("\"mensaje\":\"Listado de productos desde la Base de Datos\",");
            respuesta.append("\"total\":").append(lista.size()).append(",");
            respuesta.append("\"productos\":[");

            for (int i = 0; i < lista.size(); i++) {
                if (i > 0) {
                    respuesta.append(",");
                }
                Producto p = lista.get(i);
                
                // Armamos el JSON manualmente incluyendo el ID que viene de la BD
                respuesta.append("{");
                respuesta.append("\"id\":").append(p.getId()).append(",");
                respuesta.append("\"nombre\":\"").append(p.getNombre()).append("\",");
                respuesta.append("\"precio\":").append(p.getPrecio()).append(",");
                respuesta.append("\"cantidad\":").append(p.getCantidad());
                respuesta.append("}");
            }

            respuesta.append("]");
            respuesta.append("}");

            try (PrintWriter out = response.getWriter()) {
                out.print(respuesta.toString());
            }

        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"Error en la base de datos al listar\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        setAccessControlHeaders(response); // Habilitar CORS
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String nombre = request.getParameter("nombre");
        String precioTexto = request.getParameter("precio");
        String cantidadTexto = request.getParameter("cantidad");

        if (nombre == null || nombre.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"El nombre es obligatorio\"}");
            }
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto);
            int cantidad = Integer.parseInt(cantidadTexto);

            Producto producto = new Producto(nombre.trim(), precio, cantidad);
            
            // Le pasamos el objeto al DAO para que lo inserte en la BD
            int idGenerado = productoDAO.insertar(producto);

            if (idGenerado > 0) {
                producto.setId(idGenerado); // Le asignamos el ID real al objeto
                response.setStatus(HttpServletResponse.SC_OK);
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"mensaje\":\"Producto guardado correctamente en la BD\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"mensaje\":\"No se pudo guardar el producto\"}");
                }
            }

        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"Precio y cantidad deben ser numericos\"}");
            }
        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"Error en la base de datos al guardar\"}");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        setAccessControlHeaders(response); // Habilitar CORS
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // Para actualizar en BD necesitamos el ID exacto
        String idTexto = request.getParameter("id"); 
        String nombre = request.getParameter("nombre");
        String precioTexto = request.getParameter("precio");
        String cantidadTexto = request.getParameter("cantidad");

        if (idTexto == null || idTexto.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"El id es obligatorio para actualizar\"}");
            }
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            double precio = Double.parseDouble(precioTexto);
            int cantidad = Integer.parseInt(cantidadTexto);

            Producto productoActualizado = new Producto(nombre.trim(), precio, cantidad);
            
            // Ejecutamos el UPDATE en la base de datos
            boolean actualizado = productoDAO.actualizar(id, productoActualizado);

            if (actualizado) {
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"mensaje\":\"Producto actualizado correctamente\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"mensaje\":\"No existe un producto con ese id\"}");
                }
            }

        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"El id, precio y cantidad deben ser numericos\"}");
            }
        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"Error en la base de datos al actualizar\"}");
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        setAccessControlHeaders(response); // Habilitar CORS
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String idTexto = request.getParameter("id");

        if (idTexto == null || idTexto.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"El id es obligatorio para eliminar\"}");
            }
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            
            // Ejecutamos el DELETE en la base de datos
            boolean eliminado = productoDAO.eliminar(id);

            if (eliminado) {
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"mensaje\":\"Producto eliminado correctamente\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"mensaje\":\"No existe un producto con ese id\"}");
                }
            }

        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"El id debe ser numerico\"}");
            }
        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"mensaje\":\"Error en la base de datos al eliminar\"}");
            }
        }
    }
}