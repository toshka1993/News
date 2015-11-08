import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RssJTable extends JTable {
    private List<FeedMessage> entries = new ArrayList<>();
    private RssFeedTableModel tableModel = new RssFeedTableModel();

    public RssJTable(String s_connect, String listName) throws IOException {
        entries = getList(s_connect);

        tableModel.updateTable(entries);
        setModel(tableModel);
        CreateTable(tableModel, listName);
        addMouseListener();

        Timer timer = new Timer(0, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    entries = getList(s_connect);
                    tableModel.updateTable(entries);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        timer.setDelay(10000); // delay for 30 seconds
        timer.start();
    }

    public List<FeedMessage> getList(String s_conect) throws IOException {
        List<FeedMessage> entries = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(s_conect).get();
            Elements listContent = doc.select("figure.b-posts-1-item__image");

            entries.clear();
            for (Element element : listContent) {
                Element link = element.select("a[href]").first(); // a with href
                Element title = element.select("img[alt]").first();

                entries.add(new FeedMessage(title.attr("alt"), link.attr("href")));
            }
        }
        catch (Exception ex)
        {
            entries.clear();
        }
        return entries;
    }

    public void CreateTable (RssFeedTableModel model, String label){
        setShowGrid(false);
        setIntercellSpacing(new Dimension(20, 5));
        setRowHeight(30);

        getColumnModel().getColumn(0).setHeaderValue(label);

        JTableHeader header = getTableHeader();
        header.setOpaque(true);
        header.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        header.setForeground(Color.RED);
        header.setBackground(header.getBackground());
        Font titleFont = new Font("Arial", Font.BOLD, 10);
        header.setFont(titleFont);

        setTableHeader(header);

        setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            Color oddColor = new Color(0x25, 0x25, 0x25);
            Color evenColor = new Color(0x1a, 0x1a, 0x1a);
            Color titleColor = new Color(0x3a, 0xa2, 0xd7);


            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(row % 2 == 0 ? oddColor : evenColor);
                setForeground(titleColor);
                // setFont(font);
                return this;
            }
        });

        setFillsViewportHeight(true);
    }

    public void addMouseListener ()
    {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                entries = tableModel.getList();
                int row = getSelectedRow();
                try {
                    URI uri = new URI(entries.get(row).link);

                    openUri(uri);
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            private void openUri(URI uri) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException e) { /* TODO: error handling */ }
                } else { /* TODO: error handling */ }
            }

        });
    }


}
