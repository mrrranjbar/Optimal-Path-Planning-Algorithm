import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class GUI extends javax.swing.JFrame {

    public Point Source = new Point();
    public Point Target = new Point();
    public ArrayList<LinkList> rings = new ArrayList<LinkList>();
    public ArrayList<Point> pts = new ArrayList<Point>();
    public static ArrayList<Point> tempdraw = new ArrayList<Point>();//for test
    public static ArrayList<ChainOfEdge> tempCh = new ArrayList<ChainOfEdge>();// for test
    public LinkList ring;
    public Graphics g;
    public boolean ProcessIsFinished = false;
    public Process pr;
    public File file;
    public FileOutputStream fop = null;
    ArrayList<Point> path_temp = new ArrayList<Point>();

    public GUI() {
        initComponents();
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 200));
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel1MouseMoved(evt);
            }
        });
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jButton1.setText("Run");
        jButton1.setName(""); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("import");
        jButton2.setName(""); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("export");
        jButton3.setName(""); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        // TODO add your handling code here:

        if (SwingUtilities.isRightMouseButton(evt) && !ProcessIsFinished) {
            if (Source.x == 0 && Source.y == 0) {
                Source = evt.getPoint();
                repaint();
            }
        }
        if (ProcessIsFinished && SwingUtilities.isLeftMouseButton(evt)) {

        }
    }//GEN-LAST:event_jPanel1MouseClicked

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        // TODO add your handling code here:
        if (SwingUtilities.isLeftMouseButton(evt) && !ProcessIsFinished) {
            pts.add(evt.getPoint());
        }
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        // TODO add your handling code here:
        if (SwingUtilities.isLeftMouseButton(evt) && !ProcessIsFinished) {
            pts.add(evt.getPoint());
            CreatRing(pts);
            pts.clear();
            repaint();
        }

    }//GEN-LAST:event_jPanel1MouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        pr = new Process(rings, Source);
        pr.MainLoop();
        ProcessIsFinished = true;
        jButton1.setBackground(Color.red);
        path_temp = pr.MainFunction(Target);
        repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jPanel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseMoved
        // TODO add your handling code here:
        if (ProcessIsFinished) {
            Target = evt.getPoint();

            path_temp = pr.MainFunction(Target);
            repaint();
        }
    }//GEN-LAST:event_jPanel1MouseMoved

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String string = "";
        try {
            string = ReadFromFile();
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Point> tmp_points = new ArrayList<Point>();
        String[] parts = string.split("-");
        Source.x = Integer.parseInt(parts[0]);
        Source.y = Integer.parseInt(parts[1]);
        for (int i = 2; !"END".equals(parts[i]);) {
            if ("ring".equals(parts[i])) {
                CreatRing(tmp_points);
                tmp_points.clear();
                i++;
            } else {
                tmp_points.add(new Point(Integer.parseInt(parts[i]), Integer.parseInt(parts[i + 1])));
                i += 2;
            }
            if ("target".equals(parts[i])) {
                Target.x = Integer.parseInt(parts[i + 1]);
                Target.y = Integer.parseInt(parts[i + 2]);
                break;
            }
        }

repaint();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String str = "" + Source.x + "\n" + Source.y + "\n";
        for (LinkList ring : rings) {
            Node temp = ring.head;
            do {
                str += "" + temp.op.x + "\n" + temp.op.y + "\n";
                temp = temp.Next;
            } while (temp != ring.head);
            str += "ring\n";
        }
        str += "target\n" + Target.x + "\n" + Target.y + "\nEND\n";
        try {
            WriteToFile(str);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    public void CreatRing(ArrayList<Point> points) {
        ring = new LinkList();
        for (Point pt : points) {
            ring.insert(pt);
        }
        rings.add(ring);

    }

    public void DrawShortestPath(ArrayList<Point> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            g = this.jPanel1.getGraphics();
            g.setColor(Color.GREEN);
            g.drawLine(path.get(i).x, path.get(i).y, path.get(i + 1).x, path.get(i + 1).y);
        }
    }
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    public void paint(Graphics g1) {
        super.paint(g1);
        g = jPanel1.getGraphics();
        g.clearRect(0, 0, jPanel1.getWidth(), jPanel1.getHeight());
        g.setColor(Color.red);
        g.fillRect(Source.x, Source.y, 2, 2);
        g.setColor(Color.black);
        for (LinkList ring : rings) {
            Node temp = ring.head;
            do {
                g.drawLine(temp.op.x, temp.op.y, temp.Next.op.x, temp.Next.op.y);
                temp = temp.Next;
            } while (temp != ring.head);

        }
        if (!path_temp.isEmpty()) {
            DrawShortestPath(path_temp);
        }
    }

    public void WriteToFile(String str) throws FileNotFoundException, IOException {
        try {
            String workingDirectory = System.getProperty("user.dir");
            file = new File(workingDirectory+"//test_file.txt");
            fop = new FileOutputStream(file);

            if (!file.exists()) {
                file.createNewFile();
            }
            // get the content in bytes
            byte[] contentInBytes = str.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String ReadFromFile() throws FileNotFoundException, IOException {
        String everything;
       // File currentDir = new File("test_file.txt");
        String workingDirectory = System.getProperty("user.dir");
        BufferedReader br = new BufferedReader(new FileReader(workingDirectory+"//test_file.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("-");
                line = br.readLine();
            }
            everything = sb.toString();

        } finally {
            br.close();
        }
        return everything;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
