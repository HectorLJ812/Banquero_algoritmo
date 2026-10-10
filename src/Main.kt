
import java.awt.*
import javax.swing.*

class AlgoritmoBanquero : JFrame() {

    private val cantidadProcesos = 5
    private val cantidadRecursos = 3

    private val procesosUI = arrayOfNulls<JLabel>(5)

    private val txtMensajes = JTextArea()

    private val btnSeguro = JButton("Estado Seguro")
    private val btnInseguro = JButton("Estado Inseguro")

    private val verde = Color(46, 139, 87)
    private val rojo = Color(178, 34, 34)

    private var asignados = Array(5) { IntArray(3) }
    private var maximos = Array(5) { IntArray(3) }
    private var disponibles = IntArray(3)

    init {
        title = "Algoritmo_Banquero"
        setSize(800, 580)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout(12, 12)

        val titulo = JLabel(
            "Algoritmo Banquero",
            SwingConstants.CENTER
        )
        titulo.font = Font("Segoe UI", Font.BOLD, 20)
        titulo.border = BorderFactory.createEmptyBorder(
            15, 10, 5, 10
        )
        add(titulo, BorderLayout.NORTH)

        val panelProcesos = JPanel(GridLayout(1, 5, 10, 0))
        panelProcesos.border =
            BorderFactory.createEmptyBorder(10, 15, 10, 15)

        for (i in 0 until cantidadProcesos) {
            val label = JLabel(
                "<html><center>Proceso N$i<br>Esperando</center></html>",
                SwingConstants.CENTER
            )

            prepararProceso(label)
            procesosUI[i] = label
            panelProcesos.add(label)
        }

        txtMensajes.isEditable = false
        txtMensajes.font = Font("Monospaced", Font.PLAIN, 14)

        val scrollMensajes = JScrollPane(txtMensajes)

        val panelCentro = JPanel(BorderLayout(10, 10))
        panelCentro.border =
            BorderFactory.createEmptyBorder(0, 15, 10, 15)

        panelCentro.add(panelProcesos, BorderLayout.NORTH)
        panelCentro.add(scrollMensajes, BorderLayout.CENTER)

        add(panelCentro, BorderLayout.CENTER)

        val panelBotones = JPanel(GridLayout(1, 2, 10, 0))
        panelBotones.border =
            BorderFactory.createEmptyBorder(0, 15, 15, 15)

        prepararBoton(btnSeguro, verde)
        prepararBoton(btnInseguro, rojo)

        btnSeguro.addActionListener {
            iniciarSimulacion(true)
        }

        btnInseguro.addActionListener {
            iniciarSimulacion(false)
        }

        panelBotones.add(btnSeguro)
        panelBotones.add(btnInseguro)

        add(panelBotones, BorderLayout.SOUTH)
    }

    private fun prepararProceso(label: JLabel) {
        label.isOpaque = true
        label.background = Color(220, 245, 220)
        label.foreground = Color(0, 100, 0)
        label.font = Font("Segoe UI", Font.BOLD, 12)
        label.border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(verde, 2),
            BorderFactory.createEmptyBorder(10, 2, 10, 2)
        )
    }

    private fun prepararBoton(
        boton: JButton,
        color: Color
    ) {
        boton.background = color
        boton.foreground = Color.WHITE
        boton.font = Font("Segoe UI", Font.BOLD, 16)
        boton.isFocusPainted = false
        boton.preferredSize = Dimension(0, 45)
    }

    private fun cargarEstadoSeguro() {

        asignados = arrayOf(
            intArrayOf(0, 1, 0),
            intArrayOf(2, 0, 0),
            intArrayOf(3, 0, 2),
            intArrayOf(2, 1, 1),
            intArrayOf(0, 0, 2)
        )

        maximos = arrayOf(
            intArrayOf(7, 5, 3),
            intArrayOf(3, 2, 2),
            intArrayOf(9, 0, 2),
            intArrayOf(2, 2, 2),
            intArrayOf(4, 3, 3)
        )

        disponibles = intArrayOf(3, 3, 2)
    }

    private fun cargarEstadoInseguro() {

        asignados = arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        )

        maximos = arrayOf(
            intArrayOf(1, 1, 0),
            intArrayOf(0, 1, 1),
            intArrayOf(1, 0, 1),
            intArrayOf(1, 1, 0),
            intArrayOf(0, 1, 1)
        )

        disponibles = intArrayOf(0, 0, 0)
    }

    private fun calcularNecesidad(): Array<IntArray> {
        return Array(cantidadProcesos) { i ->
            IntArray(cantidadRecursos) { j ->
                maximos[i][j] - asignados[i][j]
            }
        }
    }

    private fun iniciarSimulacion(seguro: Boolean) {

        btnSeguro.isEnabled = false
        btnInseguro.isEnabled = false

        txtMensajes.text = ""

        if (seguro) {
            cargarEstadoSeguro()
        } else {
            cargarEstadoInseguro()
        }

        reiniciarProcesos()

        Thread {
            ejecutarBanquero()
        }.start()
    }

    private fun ejecutarBanquero() {

        val necesidad = calcularNecesidad()
        val trabajo = disponibles.copyOf()

        val finalizados = BooleanArray(cantidadProcesos)
        val secuenciaSegura = mutableListOf<Int>()

        mostrarMensaje("Iniciando Algoritmo del Banquero...")
        mostrarMensaje("Recursos disponibles: ${trabajo.joinToString(", ")}")
        mostrarMensaje("\n")

        var progreso: Boolean

        do {
            progreso = false

            for (i in 0 until cantidadProcesos) {

                if (!finalizados[i]) {

                    val puedeEjecutarse =
                        (0 until cantidadRecursos).all { j ->
                            necesidad[i][j] <= trabajo[j]
                        }

                    if (puedeEjecutarse) {

                        mostrarMensaje(
                            "Proceso N$i tiene recursos suficientes."
                        )

                        cambiarEstado(
                            i,
                            "Ejecutando",
                            Color(255, 235, 180),
                            Color(130, 90, 0)
                        )

                        Thread.sleep(1200)

                        for (j in 0 until cantidadRecursos) {
                            trabajo[j] += asignados[i][j]
                        }

                        finalizados[i] = true
                        secuenciaSegura.add(i)
                        progreso = true

                        cambiarEstado(
                            i,
                            "Finalizado",
                            Color(220, 245, 220),
                            Color(0, 100, 0)
                        )

                        mostrarMensaje(
                            ">>> Proceso N$i finalizado."
                        )

                        mostrarMensaje(
                            "Recursos liberados: " +
                                    asignados[i].joinToString(", ")
                        )

                        mostrarMensaje(
                            "Recursos disponibles: " +
                                    trabajo.joinToString(", ")
                        )

                        mostrarMensaje("\n")

                        Thread.sleep(700)
                    }
                }
            }

        } while (progreso)

        if (finalizados.all { it }) {

            mostrarMensaje("Resultado: Estado seguro")

            mostrarMensaje(
                "Secuencia segura: " +
                        secuenciaSegura.joinToString(" -> ") { "N$it" }
            )

            mostrarMensaje(
                "Todos los procesos pueden finalizar."
            )

        } else {

            for (i in 0 until cantidadProcesos) {
                if (!finalizados[i]) {
                    cambiarEstado(
                        i,
                        "Bloqueado",
                        Color(255, 220, 220),
                        rojo
                    )
                }
            }

            mostrarMensaje("Resultado: Estado inseguro")
            mostrarMensaje(
                "No existe una secuencia segura completa..."
            )
            mostrarMensaje(
                "No hay recursos suficientes para continuar..."
            )

            val pendientes = (0 until cantidadProcesos)
                .filter { !finalizados[it] }
                .joinToString(", ") { "N$it" }

            mostrarMensaje("Procesos pendientes: $pendientes")
        }

        SwingUtilities.invokeLater {
            btnSeguro.isEnabled = true
            btnInseguro.isEnabled = true
        }
    }

    private fun reiniciarProcesos() {
        for (i in 0 until cantidadProcesos) {
            cambiarEstado(
                i,
                "Esperando",
                Color(220, 245, 220),
                Color(0, 100, 0)
            )
        }
    }

    private fun cambiarEstado(
        proceso: Int,
        estado: String,
        fondo: Color,
        texto: Color
    ) {
        SwingUtilities.invokeLater {
            procesosUI[proceso]?.apply {
                background = fondo
                foreground = texto
                text = "<html><center>Proceso N$proceso<br>$estado</center></html>"
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        SwingUtilities.invokeLater {
            txtMensajes.append("$mensaje\n")
            txtMensajes.caretPosition =
                txtMensajes.document.length
        }
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val ventana = AlgoritmoBanquero()
        ventana.isVisible = true
    }
}
