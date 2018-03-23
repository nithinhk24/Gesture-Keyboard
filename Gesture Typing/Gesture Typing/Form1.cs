using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO.Ports;
using Gesture_Typing.Serial;

namespace Gesture_Typing
{
    public partial class Form1 : Form
    {
        SerialSettings currentSerialSettings;
        public string ReturnValue1 { get; set; }
        public int ReturnValue2 { get; set; }
        public int ReturnValue3 { get; set; }
        public Parity ReturnValue4 { get; set; }
        public StopBits ReturnValue5 { get; set; }

        public Form1()
        {
            InitializeComponent();

            UserInitialization();
        }

        private void UserInitialization()
        {
            currentSerialSettings = new SerialSettings();
            currentSerialSettings.PortNameCollection = SerialPort.GetPortNames();
            if (currentSerialSettings.PortNameCollection.Length > 0)
                currentSerialSettings.PortName = currentSerialSettings.PortNameCollection[0];
            portNameComboBox.DataSource = currentSerialSettings.PortNameCollection;
            baudRateComboBox.DataSource = currentSerialSettings.BaudRateCollection;
            dataBitsComboBox.DataSource = currentSerialSettings.DataBitsCollection;
            dataBitsComboBox.SelectedItem = 8;
            parityComboBox.DataSource = Enum.GetValues(typeof(System.IO.Ports.Parity));
            parityComboBox.SelectedItem = Parity.None;
            stopBitsComboBox.DataSource = Enum.GetValues(typeof(System.IO.Ports.StopBits));
            stopBitsComboBox.SelectedItem = StopBits.One;

            this.FormClosing += new FormClosingEventHandler(MainForm_FormClosing);
        }


        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            this.Close();
        }


        // Handles the "Start Listening"-buttom click event
        private void Start_Click(object sender, EventArgs e)
        {
            int val = 0;
            
            this.ReturnValue1 = portNameComboBox.Text;
            Int32.TryParse(baudRateComboBox.Text, out val);
            this.ReturnValue2 = val;
            Int32.TryParse(dataBitsComboBox.Text, out val);
            this.ReturnValue3 = val;
            this.ReturnValue4 = (Parity)Enum.Parse(typeof(Parity), parityComboBox.Text);
            this.ReturnValue5 = (StopBits)Enum.Parse(typeof(StopBits), stopBitsComboBox.Text);
            this.DialogResult = DialogResult.OK;
            //labelStatus.Text = "Port Settings have been saved. Now close the window.";
            
        }

        // Handles the "Stop Listening"-buttom click event
        private void Cancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }


        
    }
}
