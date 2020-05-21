import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import db
import datetime

# TODO: Replace JSON_PATH here
cred = credentials.Certificate("firebase_SA.json")
firebase_admin.initialize_app(cred)
testdb = firestore.client()

# Club collection
data = {
    u'contact': u'9889766754',
    u'cover_url': 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/150px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png',
    u'description': 'Android Club of SSN',
    u'dp_url': 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/150px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png',
    u'followers': ['student1@cse.ssn.edu.in', 'student2@cse.ssn.edu.in', 'student3@cse.ssn.edu.in'],
    u'head': ['androidclubhead@cse.ssn.edu.in'],
    u'id': '121334gvs',
    u'name': 'DSC Club'
}

testdb.collection(u'club').document('121334gvs').set(data)

data = {
    u'contact': u'987654321',
    u'cover_url': 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/C-sharp-major_a-sharp-minor.svg/100px-C-sharp-major_a-sharp-minor.svg.png',
    u'description': 'Music Club of SSN',
    u'dp_url': 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Music-GClef.svg/100px-Music-GClef.svg.png',
    u'followers': ['student1@cse.ssn.edu.in', 'student2@cse.ssn.edu.in', 'student3@cse.ssn.edu.in'],
    u'head': ['music_head1@cse.ssn.edu.in', 'music_head2@cse.ssn.edu.in'],
    u'id': '11kjdg211',
    u'name': 'Music Club'
}

testdb.collection(u'club').document('11kjdg211').set(data)

data = {
    u'contact': u'987474744',
    u'cover_url': 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/GDC_onlywayaround.jpg/300px-GDC_onlywayaround.jpg',
    u'description': 'Dance Club of SSN',
    u'dp_url': 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/Nandini_Ghosal.jpg/220px-Nandini_Ghosal.jpg',
    u'followers': ['student1@cse.ssn.edu.in', 'student2@cse.ssn.edu.in', 'student3@cse.ssn.edu.in'],
    u'head': ['dance_head_1@cse.ssn.edu.in', 'dance_head_2@cse.ssn.edu.in', 'dance_head_3@cse.ssn.edu.in'],
    u'id': '2142sac2q',
    u'name': 'Dance Club'
}

testdb.collection(u'club').document('2142sac2q').set(data)

print ('Club collection added successfully')

# event collection

data = {
    u'author': u'student_uploder@ssn.edu.in',
    u'description': u'Hackathon Event with details Venue:xxx college',
    u'id': '1234wqty35',
    u'file_urls': [
        {
            u'name': 'Venue_details.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        },
        {
            u'name': 'file_2.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/150px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Hack&Tackle',
}

testdb.collection(u'event').document('1234wqty35').set(data)

data = {
    u'author': u'student_uploder@ssn.edu.in',
    u'description': u'Conference  Venue:xxx college',
    u'id': '2436hdgaj',
    u'file_urls': [
        {
            u'name': 'post2_file_1.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/en/5/5c/TechEdEmea2007Keynote.jpg'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Conference meet',
}

testdb.collection(u'event').document('2436hdgaj').set(data)

data = {
    u'author': u'student_uploder@ssn.edu.in',
    u'description': u'PaperPresentation  Venue:qwerty college',
    u'id': 'huw2415fh',
    u'file_urls': [
        {
            u'name': 'details.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Jim_DiMatteo_%282%29.jpg/220px-Jim_DiMatteo_%282%29.jpg'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Tekfest',
}

testdb.collection(u'event').document('huw2415fh').set(data)

data = {
    u'author': u'student_uploder@ssn.edu.in',
    u'description': u'Event  Venue:qwerty college',
    u'id': 'ndlw3268',
    u'file_urls': [],
    u'img_urls': [],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Tekfest',
}

testdb.collection(u'event').document('ndlw3268').set(data)

print ('Event collection added successfully')

# post_bus collection

data = {
    u'desc': u'SSN invente bus routes',
    u'title': u'14-9-2019',
    u'time': firestore.SERVER_TIMESTAMP,
    u'url': 'http://www.orimi.com/pdf-test.pdf'
}

testdb.collection(u'post_bus').document().set(data)
data = {
    u'desc': u'Bus routes for placement program',
    u'title': u'21-5-2020',
    u'time': firestore.SERVER_TIMESTAMP,
    u'url': 'http://www.orimi.com/pdf-test.pdf'
}

testdb.collection(u'post_bus').document().set(data)
data = {
    u'desc': u'Instincts special bus routes',
    u'title': u'07-3-2020',
    u'time': firestore.SERVER_TIMESTAMP,
    u'url': 'http://www.orimi.com/pdf-test.pdf'
}

testdb.collection(u'post_bus').document().set(data)

print ('post_bus collection added successfully')

# post collection

data = {
    u'author': u'teacher@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec'],
    u'description': 'Test post from SSN faculty For 3rd and 4th year',
    u'id': '98324kheyctui',
    u'file_urls': [
        {u'name': 'file1.pdf',
         u'url': 'http://www.orimi.com/pdf-test.pdf'
         },
        {
            u'name': 'file1.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/5/51/Facebook_f_logo_%282019%29.svg/214px-Facebook_f_logo_%282019%29.svg.png', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/150px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Test post from SSN faculty For 3rd and 4th year',
    u'year': {
        u'2016': True,
        u'2017': True,
        u'2018': False,
        u'2019': False
    }
}

testdb.collection(u'post').document('98324kheyctui').set(data)

data = {
    u'author': u'teacher@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec', 'it', 'bme', 'che', 'civ'],
    u'description': 'Test post from SSN faculty For all department',
    u'id': 'sa673897512',
    u'file_urls': [
        {u'name': 'file1.pdf',
         u'url': 'http://www.orimi.com/pdf-test.pdf'
         },
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Rotterdam_Ahoy_Europort_2011_%2814%29.JPG/220px-Rotterdam_Ahoy_Europort_2011_%2814%29.JPG'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Test post from SSN faculty For all department',
    u'year': {
        u'2016': True,
        u'2017': True,
        u'2018': False,
        u'2019': False
    }
}

testdb.collection(u'post').document('sa673897512').set(data)

data = {
    u'author': u'teacher@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec'],
    u'description': 'Test post from SSN faculty For 4th year only',
    u'id': '983752uewfu',
    u'file_urls': [
        {u'name': 'final_year_details.pdf',
         u'url': 'http://www.orimi.com/pdf-test.pdf'
         },
    ],
    u'img_urls': [],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Test post from SSN faculty For 4th year only',
    u'year': {
        u'2016': True,
        u'2017': False,
        u'2018': False,
        u'2019': False
    }
}

testdb.collection(u'post').document('983752uewfu').set(data)

data = {
    u'author': u'teacher@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec'],
    u'description': 'Test post from SSN faculty For all years',
    u'id': 'jdvn74',
    u'file_urls': [],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/150px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Test post from SSN faculty For all years',
    u'year': {
        u'2016': True,
        u'2017': True,
        u'2018': True,
        u'2019': True
    }
}

testdb.collection(u'post').document('jdvn74').set(data)

print ('post collection added successfully')

# placement collection

data = {
    u'author': u'placement@ssn.edu.in',
    u'dept': ['cse', 'it'],
    u'description': u'Infosys Placements details ',
    u'id': 'kajbsdv6512',
    u'file_urls': [
        {
            u'name': 'requirements.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        },
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Infosys_logo.svg/220px-Infosys_logo.svg.png'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Infosys Hiring',
}

testdb.collection(u'placement').document('kajbsdv6512').set(data)

data = {
    u'author': u'placement@ssn.edu.in',
    u'dept': ['mech', 'ece', 'eee'],
    u'description': u'Honda Placements details ',
    u'id': '89035dndskl',
    u'file_urls': [
        {
            u'name': 'requirements.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        },
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Honda_logo.svg/220px-Honda_logo.svg.png'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Honda Automobile and Electonics Hiring',
}

testdb.collection(u'placement').document('89035dndskl').set(data)

data = {
    u'author': u'placement@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec', 'it', 'bme', 'che', 'civ'],
    u'description': u'Hindustan Placements details ',
    u'id': 'skklsan8ywr',
    u'file_urls': [
        {
            u'name': 'requirements.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        },
    ],
    u'img_urls': [],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Hindustan motors Hiring',
}

testdb.collection(u'placement').document('skklsan8ywr').set(data)

data = {
    u'author': u'placement@ssn.edu.in',
    u'dept': ['bme', 'che'],
    u'description': u'Dove Placements details ',
    u'id': 'aslj38yr5',
    u'file_urls': [
        {
            u'name': 'requirements.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        },
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/Dove_wordmark.svg/220px-Dove_wordmark.svg.png'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Dove chemicals Hiring',
}

testdb.collection(u'placement').document('aslj38yr5').set(data)
data = {
    u'author': u'placement@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec', 'it', 'bme', 'che', 'civ'],
    u'description': u'Wipro Placements details ',
    u'id': 'lkdnsv1',
    u'file_urls': [],
    u'img_urls': [],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Wipro Tech Solutions Hiring',
}

testdb.collection(u'placement').document('lkdnsv1').set(data)

print ('placement collection added successfully')

# examcell collection

data = {
    u'author': u'teacher@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec'],
    u'description': 'Seating Arrangement from Exam Cell For 3rd and 4th year',
    u'id': '98324kheyctui',
    u'file_urls': [
        {u'name': 'file1.pdf',
         u'url': 'http://www.orimi.com/pdf-test.pdf'
         }
    ],
    u'img_urls': [],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Seating Arrangement from Exam Cell For 3rd and 4th year',
    u'year': {
        u'2016': True,
        u'2017': True,
        u'2018': False,
        u'2019': False
    }
}

testdb.collection(u'examcell').document('98324kheyctui').set(data)

data = {
    u'author': u'teacher@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec', 'it', 'bme', 'che', 'civ'],
    u'description': 'Seating Arrangement from Exam Cell For all department',
    u'id': 'sa673897512',
    u'file_urls': [
        {u'name': 'file1.pdf',
         u'url': 'http://www.orimi.com/pdf-test.pdf'
         },
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Rotterdam_Ahoy_Europort_2011_%2814%29.JPG/220px-Rotterdam_Ahoy_Europort_2011_%2814%29.JPG'],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Test post from SSN faculty For all department',
    u'year': {
        u'2016': True,
        u'2017': True,
        u'2018': False,
        u'2019': False
    }
}

testdb.collection(u'examcell').document('sa673897512').set(data)

data = {
    u'author': u'teacher@ssn.edu.in',
    u'dept': ['eee', 'ece', 'cse', 'mec'],
    u'description': 'Revaluation details from ExamCell For 4th year only',
    u'id': '983752uewfu',
    u'file_urls': [
        {u'name': 'final_year_details.pdf',
         u'url': 'http://www.orimi.com/pdf-test.pdf'
         },
    ],
    u'img_urls': [],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Revaluation details from ExamCell For 4th year only',
    u'year': {
        u'2016': True,
        u'2017': False,
        u'2018': False,
        u'2019': False
    }
}

testdb.collection(u'examcell').document('983752uewfu').set(data)

print ('examcell collection added successfully')

# post_club collection

data = {
    u'author': u'clubhead@ssn.edu.in',
    u'cid': '11kjdg211',
    u'description': u'Test Post from Music Club',
    u'id': 'niewoionew',
    u'comment': [],
    u'file_urls': [
        {
            u'name': 'file1.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        },
        {
            u'name': 'file2.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Music-GClef.svg/100px-Music-GClef.svg.png'],
    u'like': ['student1@ssn.edu.in', 'student2@ssn.edu.in', 'student3@ssn.edu.in', ],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Music club session in Main Audi',

}

testdb.collection(u'post_club').document('niewoionew').set(data)

data = {
    u'author': u'clubhead@ssn.edu.in',
    u'cid': '121334gvs',
    u'description': u'Test Post from DSC Club',
    u'id': 'uvesevug635',
    u'comment': [],
    u'file_urls': [
        {
            u'name': 'file1.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        },
        {
            u'name': 'file2.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/150px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png'],
    u'like': ['student1@ssn.edu.in', 'student2@ssn.edu.in', 'student3@ssn.edu.in', ],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'DCS club session in CSE seminar Hall',

}

testdb.collection(u'post_club').document('uvesevug635').set(data)

data = {
    u'author': u'clubhead@ssn.edu.in',
    u'cid': '121334gvs',
    u'description': u'Web Development classes from DSC Club',
    u'id': 'ewvthy23',
    u'comment': [],
    u'file_urls': [
        {
            u'name': 'file1.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/9/9c/GNOME_Web_logo.png'],
    u'like': ['student1@ssn.edu.in', 'student2@ssn.edu.in', 'student3@ssn.edu.in', ],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'web Development classes in CSE seminar Hall',

}

testdb.collection(u'post_club').document('ewvthy23').set(data)

data = {
    u'author': u'clubhead@ssn.edu.in',
    u'cid': '121334gvs',
    u'description': u'Android classes from DSC Club',
    u'id': 'efeseu63478',
    u'comment': [],
    u'file_urls': [
        {
            u'name': 'file1.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': ['https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/Android_robot_2014.svg/151px-Android_robot_2014.svg.png'],
    u'like': ['student1@ssn.edu.in', 'student2@ssn.edu.in', 'student3@ssn.edu.in', ],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Android classes in CSE seminar Hall',

}

testdb.collection(u'post_club').document('efeseu63478').set(data)

data = {
    u'author': u'clubhead@ssn.edu.in',
    u'cid': '2142sac2q',
    u'description': u'Test Post from Dance Club',
    u'id': 'novvyu37',
    u'comment': [],
    u'file_urls': [
        {
            u'name': 'file2.pdf',
            u'url': 'http://www.orimi.com/pdf-test.pdf'
        }
    ],
    u'img_urls': [],
    u'like': ['student1@ssn.edu.in', 'student2@ssn.edu.in', 'student3@ssn.edu.in', ],
    u'time': firestore.SERVER_TIMESTAMP,
    u'title': 'Dance club session in Main Audi',

}

testdb.collection(u'post_club').document('novvyu37').set(data)

print ('post_club collection added successfully')
