using System;
using System.Collections.Generic;
using SQLite4Unity3d;
using UnityEngine;
using MyScript.Objects;
using System.Collections;
using System.Text;
using System.IO;
#if !UNITY_EDITOR
using System.Collections;
using System.IO;
#endif

namespace ComputerApi.Controller
{
    public class SQLiteHelper
    {
        private SQLiteConnection _connection;

        public SQLiteHelper(string DatabaseName)
        {

#if UNITY_EDITOR
            var dbPath = string.Format(@"Assets/StreamingAssets/{0}", DatabaseName);
#else
        // check if file exists in Application.persistentDataPath
        var filepath = string.Format("{0}/{1}", Application.persistentDataPath, DatabaseName);

        if (!File.Exists(filepath))
        {
            Debug.Log("Database not in Persistent path");
            // if it doesn't ->
            // open StreamingAssets directory and load the db ->
#if UNITY_WINRT
			var loadDb = Application.dataPath + "/StreamingAssets/" + DatabaseName;  // this is the path to your StreamingAssets in iOS
			// then save to Application.persistentDataPath
			File.Copy(loadDb, filepath);
#endif

            Debug.Log("Database written");
        }

        var dbPath = filepath;
#endif
            if (!File.Exists(dbPath))
            {
                File.Create(dbPath);
            }
            string dbURI = "URI=file" + dbPath;
            _connection = new SQLiteConnection(dbPath, SQLiteOpenFlags.ReadWrite | SQLiteOpenFlags.Create);
            Debug.Log("Final PATH: " + dbPath);

        }

        public void CloseDB()
        {
            lock (_connection)
            {
                _connection.Close();
            }
        }

        public void CreateDB()
        {
            lock (_connection)
            {
                CreateBookCaseObjectDB();

                _connection.DropTable<VRObject>();
                _connection.CreateTable<VRObject>();
            }
        }

        private void CreateBookCaseObjectDB()
        {
            _connection.DropTable<BookCaseObject>();
            _connection.CreateTable<BookCaseObject>();

            BookCaseObject[] array = new BookCaseObject[18];
            for (int i = 0; i < 18; i++)
            {
                array[i] = new BookCaseObject("BookCaseTrigger" + i, 0, 0, i);
            }
            _connection.InsertAll(array);
        }

        public int InsertBookCaseObject(IEnumerable<BookCaseObject> enumerable)
        {
            return Insert(enumerable);
        }

        public int InsertVRObject(IEnumerable<VRObject> enumerable)
        {
            return Insert(enumerable);
        }

        private int Insert(IEnumerable enumerable)
        {
            lock (_connection)
            {
                return _connection.InsertAll(enumerable);
            }
        }

        public int UpdateBookCaseObject(IEnumerable<BookCaseObject> enumerable)
        {
            return UpdateOrInsert(enumerable);
        }

        public int UpdateVRObject(IEnumerable<VRObject> enumerable)
        {
            return UpdateOrInsert(enumerable);
        }

        private int UpdateOrInsert(IEnumerable enumerable)
        {
            int count = 0;
            lock (_connection)
            {
                foreach (object obj in enumerable)
                {
                    count += _connection.InsertOrReplace(obj);
                }
            }

            return count;
        }

        private int Update(IEnumerable enumerable)
        {
            lock (_connection)
            {
                return _connection.UpdateAll(enumerable);
            }
        }

        public IEnumerable<BookCaseObject> SelectBookCaseObject()
        {
            lock (_connection)
            {
                return _connection.Table<BookCaseObject>();
            }
        }

        public IEnumerable<VRObject> SelectVRObject()
        {
            lock (_connection)
            {
                return _connection.Table<VRObject>();
            }
        }

        public string QueryAllVRObject()
        {
            IEnumerable<VRObject> enumerable = SelectVRObject();

            StringBuilder sb = new StringBuilder();
            foreach (VRObject obj in enumerable)
            {
                sb.Append("{" + obj.ToJSON() + "},");
            }
            sb.Remove(sb.Length - 1, 1);

            return sb.ToString();
        }

        public string QueryAllBookCaseObject()
        {
            IEnumerable<BookCaseObject> enumerable = SelectBookCaseObject();

            StringBuilder sb = new StringBuilder();
            foreach (BookCaseObject obj in enumerable)
            {
                sb.Append("{" + obj.ToJSON() + "},");
            }
            sb.Remove(sb.Length - 1, 1);

            return sb.ToString();
        }

        /*
        public IEnumerable<Person> GetPersons()
        {
            return _connection.Table<Person>();
        }

        public IEnumerable<Person> GetPersonsNamedRoberto()
        {
            return _connection.Table<Person>().Where(x => x.Name == "Roberto");
        }

        public Person GetJohnny()
        {
            return _connection.Table<Person>().Where(x => x.Name == "Johnny").FirstOrDefault();
        }

        public Person CreatePerson()
        {
            var p = new Person
            {
                Name = "Johnny",
                Surname = "Mnemonic",
                Age = 21
            };
            _connection.Insert(p);
            return p;
        }

        */
    }
}
