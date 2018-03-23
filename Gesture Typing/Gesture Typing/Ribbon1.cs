using System;
using System.Collections.Generic;
using System.IO.Ports;
using System.Linq;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using Microsoft.Office.Tools.Ribbon;
using word = Microsoft.Office.Interop.Word;

namespace Gesture_Typing
{
    public partial class Ribbon1
    {
        SerialPort mserialPort;
        word.Application currentApplication;
        word.Document currentDocument;
        private String tString = String.Empty;

        private void Ribbon1_Load(object sender, RibbonUIEventArgs e)
        {

        }

        private void initialize_variables()
        {
            currentApplication = Globals.ThisAddIn.Application;
            currentDocument = currentApplication.ActiveDocument;
        }

        public void print_data(string data)
        {
            currentApplication.Selection.TypeText(data);
        }

        private void buttonStart_Click(object sender, RibbonControlEventArgs e)
        {
            initialize_variables();
            mserialPort = new SerialPort();
            using (var form = new Form1())
            {
                var result = form.ShowDialog();
                if (result == DialogResult.OK)
                {
                    mserialPort.PortName = form.ReturnValue1;         //values preserved after close
                    mserialPort.BaudRate = form.ReturnValue2;
                    mserialPort.DataBits = form.ReturnValue3;
                    mserialPort.Parity = form.ReturnValue4;
                    mserialPort.StopBits = form.ReturnValue5;

                    mserialPort.DataReceived += new SerialDataReceivedEventHandler(ReceivedData);
                    mserialPort.Open();
                    buttonStart.Enabled = false;
                    buttonStop.Enabled = true;
                }
            }

        }

        private void buttonStop_Click(object sender, RibbonControlEventArgs e)
        {
            mserialPort.Close();
            buttonStart.Enabled = true;
            buttonStop.Enabled = false;
        }

        
         private void ReceivedData(object sender, SerialDataReceivedEventArgs e)
         {
             int nBytes = mserialPort.BytesToRead;
             byte[] buffer = new byte[nBytes];
             int bytesRead = mserialPort.Read(buffer, 0, nBytes);
             tString = Encoding.ASCII.GetString(buffer, 0, bytesRead);
             this.print_data(tString);
         }
    }
}