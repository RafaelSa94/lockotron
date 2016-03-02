#ifndef FACEOTRON_GLOBAL_H
#define FACEOTRON_GLOBAL_H

#include <QtCore/qglobal.h>

#if defined(FACEOTRON_LIBRARY)
#  define FACEOTRONSHARED_EXPORT Q_DECL_EXPORT
#else
#  define FACEOTRONSHARED_EXPORT Q_DECL_IMPORT
#endif

#endif // FACEOTRON_GLOBAL_H
